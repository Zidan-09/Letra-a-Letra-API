import { AuthFlow } from "./flows/auth.flow.js";
import { User } from "./models/User.js";
import { connect, send } from "./core/websocket.js";
import { waitForEvent } from "./core/waitForEvent.js";
import { sleep } from "./core/sleep.js";
import {
    chooseAction,
    clearExpiredAfflictions,
    createMemory,
    createRng,
    observeResult,
    toPayload,
    updateAfflictions
} from "./bot/strategy.js";
import { fileURLToPath } from "url";
import path from "path";

const ACK_TIMEOUT_MS = 15000;

function parseArgs() {
    const args = process.argv.slice(2);
    const options = { nickname: null, gameMode: "NORMAL", delayMs: 1000, seed: 42, powerChance: 0.4 };

    for (const arg of args) {
        if (arg === "--help" || arg === "-h" || arg === "help") {
            printHelp();
            process.exit(0);
        } else if (arg.startsWith("--nickname=")) {
            options.nickname = arg.split("=")[1];
        } else if (arg.startsWith("--mode=") || arg.startsWith("--gameMode=")) {
            options.gameMode = arg.split("=")[1];
        } else if (arg.startsWith("--delay=")) {
            options.delayMs = Number(arg.split("=")[1]) || 1000;
        } else if (arg.startsWith("--seed=")) {
            options.seed = Number(arg.split("=")[1] ?? 42);
        } else if (arg.startsWith("--powerChance=")) {
            const v = Number(arg.split("=")[1]);
            options.powerChance = Number.isFinite(v) ? Math.min(1, Math.max(0, v)) : 0.4;
        } else if (!arg.startsWith("--")) {
            if (!options.nickname) options.nickname = arg;
            else options.gameMode = arg;
        }
    }

    if (!options.nickname) {
        options.nickname = `Bot-${Math.floor(Math.random() * 9000) + 1000}`;
    }

    return options;
}

function printHelp() {
    console.log(`
Bot Letra-a-Letra — joga contra uma pessoa real via matchmaking.

Usage:
  node tools/bot.js [nickname] [gameMode]
  node tools/bot.js --nickname=Bot1 --mode=NORMAL --delay=1000 --seed=42 --powerChance=0.4

Args:
  nickname      nome do bot (default: Bot-XXXX aleatório)
  gameMode      modo de matchmaking (default: NORMAL)
  --delay       pausa antes de cada jogada em ms (default: 1000)
  --seed        seed do RNG para jogadas reproduzíveis (default: 42)
  --powerChance chance de usar poder ofensivo quando aplicável 0-1 (default: 0.4)

O bot revela células e usa poderes do inventário (BLOCK, TRAP, SPY,
FREEZE, BLIND, UNBLOCK, DETECT_TRAPS, UNFREEZE, LANTERN, IMMUNITY)
pelo mesmo fluxo de um jogador real (PLAYER_ACTION).

O outro jogador deve entrar na fila com o MESMO gameMode
(type MATCHMAKING_GAME) pelo cliente real para a partida ser formada.
`);
}

function observeGameEvent(memory, event) {
    if (Array.isArray(event?.events)) {
        updateAfflictions(memory.afflictions, event.events);
    }
    if (event?.data) {
        clearExpiredAfflictions(memory.afflictions, event.data, memory.myId);
    }
}

function describeAction(action) {
    if (action.type === "PASS") return `PASS (${action.reason})`;
    const target = action.position ? `(${action.position.x}, ${action.position.y})` :
        action.targetId ? `target=${String(action.targetId).slice(0, 8)}` : "";
    return `${action.type} ${target} actionId=${action.actionId ? String(action.actionId).slice(0, 8) : "-"}`;
}

function drainStaleAcks(events, myId, memory) {
    for (let i = events.length - 1; i >= 0; i--) {
        const e = events[i];
        if (e?.event === "ERROR" ||
            ((e?.event === "PLAYER_ACTION_RESULT" || e?.event === "TURN_EXPIRED") &&
                e?.data?.currentTurnPlayerId !== myId)) {
            if (memory) observeGameEvent(memory, e);
            events.splice(i, 1);
        }
    }
}

async function playOneTurn(ws, events, gameId, gameData, memory, rng, powerChance) {
    const action = chooseAction({ gameData, myId: memory.myId, memory, rng, powerChance });
    observeResult(memory, action, memory.myId);

    const payload = toPayload(gameId, action);
    if (!payload) {
        console.log(`[bot] ${describeAction(action)} — aguardando (sem envio, como um jogador real faria).`);
        return;
    }

    drainStaleAcks(events, memory.myId, memory);
    console.log(`[bot] ${describeAction(action)} gameId=${gameId}`);
    send(ws, payload);

    let ack;
    try {
        ack = await waitForEvent(
            "ACTION_ACK",
            e => e.event === "ERROR" ||
                e.event === "GAME_OVER" ||
                e.event === "REMOVED_BECAUSE_INACTIVITY" ||
                ((e.event === "PLAYER_ACTION_RESULT" || e.event === "TURN_EXPIRED") &&
                    e.data?.currentTurnPlayerId !== memory.myId),
            events,
            ACK_TIMEOUT_MS
        );
    } catch {
        console.log("[bot] sem confirmação do servidor (timeout); seguindo para aguardar turno.");
        return;
    }

    observeGameEvent(memory, ack);

    if (ack.event === "ERROR") {
        const msg = String(ack.message ?? JSON.stringify(ack));
        console.log(`[bot] jogada rejeitada pelo servidor: ${msg} — tentando fallback para REVEAL.`);
        if (/frozen|congel/i.test(msg)) memory.afflictions.frozen.add(memory.myId);
        if (/blind|cego|cégo|cego/i.test(msg)) memory.afflictions.blind.add(memory.myId);
        const fallback = chooseAction({ gameData, myId: memory.myId, memory, rng, powerChance: 0 });
        const retry = toPayload(gameId, fallback);
        if (retry) {
            console.log(`[bot] fallback: ${describeAction(fallback)}`);
            observeResult(memory, fallback, memory.myId);
            send(ws, retry);
            try {
                const retryAck = await waitForEvent(
                    "ACTION_ACK_RETRY",
                    e => e.event === "ERROR" ||
                        e.event === "GAME_OVER" ||
                        e.event === "REMOVED_BECAUSE_INACTIVITY" ||
                        ((e.event === "PLAYER_ACTION_RESULT" || e.event === "TURN_EXPIRED") &&
                            e.data?.currentTurnPlayerId !== memory.myId),
                    events,
                    ACK_TIMEOUT_MS
                );
                observeGameEvent(memory, retryAck);
            } catch {
                console.log("[bot] sem confirmação do fallback (timeout); seguindo para aguardar turno.");
            }
        } else {
            console.log("[bot] fallback PASS — aguardando turno expirar.");
        }
    }
}

export async function runBot({ nickname, gameMode, delayMs, seed, powerChance }) {
    const user = new User(nickname, `${nickname.toLowerCase()}@email.com`, "12345678");
    const events = [];
    const memory = createMemory();
    const rng = createRng(seed);
    let ws;

    try {
        try {
            await AuthFlow.register(user);
        } catch (e) {
            const msg = String(e.message).toLowerCase();
            const isDuplicate = msg.includes("already") || msg.includes("em uso") || msg.includes("duplicate");
            if (!isDuplicate) throw e;
        }
        await AuthFlow.login(user);
        memory.myId = user.id;
        console.log(`[bot] logado como ${user.nickname} (${user.id}) seed=${seed} powerChance=${powerChance}`);

        ws = await connect(user, events);
        console.log(`[bot] websocket conectado. Entrando na fila ${gameMode}...`);
        console.log(`[bot] aguardando jogador real entrar com MATCHMAKING_GAME + gameMode=${gameMode}`);

        send(ws, { type: "MATCHMAKING_GAME", gameMode });

        const started = await waitForEvent(
            "MATCHMAKING_GAME FOUNDED",
            e => e.event === "MATCHMAKING_GAME" && e.status === "FOUNDED",
            events
        );

        const gameId = started.gameId ?? started.data?.gameId;
        if (!gameId) {
            throw new Error(`[bot] gameId ausente em ${JSON.stringify(started)}`);
        }

        let gameData = started.data;
        observeGameEvent(memory, started);
        console.log(`[bot] partida encontrada! gameId=${gameId}`);
        console.log(`[bot] meu id=${user.id} | turno atual=${gameData?.currentTurnPlayerId} | ${gameData?.currentTurnPlayerId === user.id ? "EU COMEÇO" : "OPONENTE COMEÇA"}`);

        if (gameData?.currentTurnPlayerId === user.id) {
            await sleep(delayMs);
            await playOneTurn(ws, events, gameId, gameData, memory, rng, powerChance);
        }

        let running = true;
        while (running) {
            const result = await waitForEvent(
                "GAME_OVER / PLAYER_ACTION_RESULT / TURN_EXPIRED",
                e =>
                    e.event === "GAME_OVER" ||
                    e.event === "REMOVED_BECAUSE_INACTIVITY" ||
                    ((e.event === "PLAYER_ACTION_RESULT" || e.event === "TURN_EXPIRED") &&
                        e.data?.currentTurnPlayerId === user.id),
                events
            );

            if (result.event === "GAME_OVER") {
                console.log(`[bot] GAME_OVER: ${JSON.stringify(result.data ?? result)}`);
                running = false;
                break;
            }

            if (result.event === "REMOVED_BECAUSE_INACTIVITY") {
                console.log(`[bot] REMOVED_BECAUSE_INACTIVITY: ${JSON.stringify(result)}`);
                running = false;
                break;
            }

            observeGameEvent(memory, result);
            gameData = result.data;
            console.log(`[bot] meu turno! inventário=${JSON.stringify(gameData?.players?.find(p => p.id === user.id)?.inventory ?? [])}`);
            await sleep(delayMs);
            await playOneTurn(ws, events, gameId, gameData, memory, rng, powerChance);
        }
    } finally {
        try { ws?.close(); } catch {}
    }
}

const __filename = fileURLToPath(import.meta.url);
const isDirectRun = process.argv[1] && path.resolve(process.argv[1]) === path.resolve(__filename);

if (isDirectRun) {
    const options = parseArgs();
    console.log(`\n-------- Bot vs Humano --------\nBot: ${options.nickname} | mode: ${options.gameMode} | delay: ${options.delayMs}ms | seed: ${options.seed}\n`);
    let exitCode = 0;
    try {
        await runBot(options);
        console.log("\n✅ Bot finalizado.");
    } catch (e) {
        console.error("\n❌ Bot falhou:");
        console.error(e);
        exitCode = 1;
    } finally {
        setTimeout(() => process.exit(exitCode), 500);
    }
}
