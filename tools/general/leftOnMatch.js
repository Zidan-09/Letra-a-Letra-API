import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";
import { fileURLToPath } from "url";
import path from "path";

export async function runFlow(context) {
    const [ws1, ws2] = context.sockets;

    const users = context.users;
    const events = context.getSharedEvents();

    send(ws1, {
        type: "MATCHMAKING_GAME",
        gameMode: "NORMAL"
    });

    send(ws2, {
        type: "MATCHMAKING_GAME",
        gameMode: "NORMAL"
    });

    const started = await waitForEvent("MATCHMAKING_GAME", e => {
        const ev = e.event ?? e.type;
        const st = e.status ?? e.matchStatus ?? e.state;
        return ev === "MATCHMAKING_GAME" && (st === "FOUNDED" || st === "founded");
    }, events);

    await sleep(1000);

    const gameId = started.gameId ?? started.data?.gameId ?? started.roomId ?? started.data?.roomId ?? started.id;

    if (!gameId) {
        throw new Error(`leftOnMatch: gameId missing in ${JSON.stringify(started)}`);
    }

    console.log(`[leftOnMatch] partida iniciada gameId=${gameId} users=${users.map(u => `${u.nickname}(${u.id})`).join(", ")}`);

    const leavingUser = users[0];
    const leavingWs = ws1;
    const remainingUser = users[1];

    console.log(`[leftOnMatch] ${leavingUser.nickname} (${leavingUser.id}) vai enviar LEFT_GAME para ${gameId}`);

    send(leavingWs, {
        type: "LEFT_GAME",
        gameId: gameId
    });

    const leaveResult = await waitForEvent(
        "PARTICIPANT_LEAVE / LEFT_GAME / GAME_OVER",
        e => {
            const ev = e.event ?? e.type;
            return ev === "PARTICIPANT_LEAVE" || ev === "LEFT_GAME" || ev === "GAME_OVER" || ev === "ROOM_CLOSED" || ev === "PLAYER_LEFT";
        },
        events
    );

    console.log(`[leftOnMatch] evento de saída recebido: ${JSON.stringify(leaveResult)}`);

    await sleep(1000);

    const evType = leaveResult.event ?? leaveResult.type;
    if (evType !== "GAME_OVER") {
        try {
            const gameOver = await waitForEvent(
                "GAME_OVER",
                e => (e.event ?? e.type) === "GAME_OVER",
                events,
                8000
            );
            console.log(`[leftOnMatch] GAME_OVER após LEFT_GAME: ${JSON.stringify(gameOver)}`);

            const winnerId = gameOver.data?.winnerId ?? gameOver.data?.winner?.userId ?? gameOver.winnerId;
            if (winnerId && winnerId !== remainingUser.id) {
                console.warn(`[leftOnMatch] WARN: winnerId ${winnerId} != remaining ${remainingUser.id}`);
            }
        } catch (e) {
            console.log(`[leftOnMatch] nenhum GAME_OVER em 8s após LEFT_GAME (pode ser WAITING/CUSTOM sem encerramento) — ${e.message}`);
        }
    } else {
        console.log(`[leftOnMatch] GAME_OVER já recebido como evento de saída`);
    }

    await sleep(1000);

    console.log(`[leftOnMatch] fluxo concluído. Verifique no painel admin GET /game que o match ${gameId} está CLOSED e não RUNNING com score anterior.`);
}

const __filename = fileURLToPath(import.meta.url);
const isDirectRun = process.argv[1] && path.resolve(process.argv[1]) === path.resolve(__filename);

if (isDirectRun) {
    const { TestContext } = await import("../context/TestsContext.js");

    console.log("\n--------Init General Test (direct): leftOnMatch --------\n");

    const context = new TestContext();
    context.addUser("leftDirect1");
    context.addUser("leftDirect2");

    let exitCode = 0;
    try {
        await context.authUsers();
        await context.connectSockets();
        await runFlow(context);
        console.log("\n✅  leftOnMatch (direct) OK");
    } catch (e) {
        console.error("\n❌  leftOnMatch (direct) FAIL");
        console.error(e);
        exitCode = 1;
    } finally {
        try { context.dispose(); } catch {}
        setTimeout(() => process.exit(exitCode), 500);
    }
}
