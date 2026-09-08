import { AuthFlow } from "./flows/auth.flow.js";
import { User } from "./models/User.js";
import { connect, send } from "./core/websocket.js";
import { waitForEvent } from "./core/waitForEvent.js";
import { sleep } from "./core/sleep.js";
import { fileURLToPath } from "url";
import path from "path";

function parseArgs() {
    const args = process.argv.slice(2);
    const options = { nickname: null, gameMode: "NORMAL", delayMs: 1000 };

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
  node tools/bot.js --nickname=Bot1 --mode=NORMAL --delay=1000

Args:
  nickname   nome do bot (default: Bot-XXXX aleatório)
  gameMode   modo de matchmaking (default: NORMAL)

O outro jogador deve entrar na fila com o MESMO gameMode
(type MATCHMAKING_GAME) pelo cliente real para a partida ser formada.
`);
}

function buildPositions(size = 10) {
    const positions = [];
    for (let x = 0; x < size; x++) {
        for (let y = 0; y < size; y++) {
            positions.push({ x, y });
        }
    }
    return positions;
}

function removeRevealedFromPool(pool, board) {
    if (!Array.isArray(board)) return;
    for (let x = 0; x < board.length; x++) {
        const row = board[x];
        if (!Array.isArray(row)) continue;
        for (let y = 0; y < row.length; y++) {
            const cell = row[y];
            if (cell?.revealed) {
                const idx = pool.findIndex(p => p.x === x && p.y === y);
                if (idx !== -1) pool.splice(idx, 1);
            }
        }
    }
}

function pickRandom(pool) {
    if (pool.length === 0) return null;
    return pool.splice(Math.floor(Math.random() * pool.length), 1)[0];
}

export async function runBot({ nickname, gameMode, delayMs }) {
    const user = new User(nickname, `${nickname.toLowerCase()}@email.com`, "12345678");
    const events = [];
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
        console.log(`[bot] logado como ${user.nickname} (${user.id})`);

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

        let currentPlayer = started.data?.currentTurnPlayerId;
        console.log(`[bot] partida encontrada! gameId=${gameId}`);
        console.log(`[bot] meu id=${user.id} | turno atual=${currentPlayer} | ${currentPlayer === user.id ? "EU COMEÇO" : "OPONENTE COMEÇA"}`);

        const pool = buildPositions(10);
        removeRevealedFromPool(pool, started.data?.board);

        if (currentPlayer === user.id) {
            await sleep(delayMs);
            await playTurn(ws, gameId, pool);
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

            removeRevealedFromPool(pool, result.data?.board);

            const isMyTurn = result.data?.currentTurnPlayerId === user.id;
            if (!isMyTurn) continue;

            console.log(`[bot] meu turno! células restantes=${pool.length}`);
            await sleep(delayMs);
            const ok = await playTurn(ws, gameId, pool);
            if (!ok) {
                console.log("[bot] sem posições restantes, encerrando.");
                running = false;
                break;
            }
        }
    } finally {
        try { ws?.close(); } catch {}
    }
}

async function playTurn(ws, gameId, pool) {
    const pos = pickRandom(pool);
    if (!pos) return false;

    console.log(`[bot] REVEAL (${pos.x}, ${pos.y}) gameId=${gameId}`);
    send(ws, {
        type: "PLAYER_ACTION",
        gameId,
        action: { type: "REVEAL", position: pos }
    });
    return true;
}

const __filename = fileURLToPath(import.meta.url);
const isDirectRun = process.argv[1] && path.resolve(process.argv[1]) === path.resolve(__filename);

if (isDirectRun) {
    const options = parseArgs();
    console.log(`\n-------- Bot vs Humano --------\nBot: ${options.nickname} | mode: ${options.gameMode} | delay: ${options.delayMs}ms\n`);
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
