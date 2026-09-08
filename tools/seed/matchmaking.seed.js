import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";

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
        throw new Error(`matchmaking: gameId missing in ${JSON.stringify(started)}`);
    }

    let currentPlayer = started.data?.currentTurnPlayerId ?? started.data?.currentPlayerId ?? started.currentTurnPlayerId;

    const positions = [];
    for (let x = 0; x < 10; x++) {
        for (let y = 0; y < 10; y++) {
            positions.push({ x, y });
        }
    }

    let gameRunning = true;

    while (gameRunning) {
        if (positions.length === 0) {
            gameRunning = false;
            break;
        }

        const pos = positions.splice(Math.floor(Math.random() * positions.length), 1)[0];

        const currentWs =
            currentPlayer === users[0].id ? ws1 : ws2;

        send(currentWs, {
            type: "PLAYER_ACTION",
            gameId: gameId,
            action: {
                type: "REVEAL",
                position: pos
            }
        });

        const result = await waitForEvent(
            "GAME_OVER / PLAYER_ACTION_RESULT",
            e => {
                const ev = e.event ?? e.type;
                if (ev === "GAME_OVER") return true;
                if (ev === "PLAYER_ACTION_RESULT") {
                    const pid = e.data?.currentTurnPlayerId ?? e.data?.currentPlayerId ?? e.data?.nextTurnPlayerId;
                    return pid && pid !== currentPlayer;
                }
                return false;
            },
                events
        );

        await sleep(125);

        const evResult = result.event ?? result.type;
        if (evResult === "GAME_OVER") {
            gameRunning = false;
            break;
        }

        currentPlayer = result.data?.currentTurnPlayerId ?? result.data?.currentPlayerId ?? result.data?.nextTurnPlayerId;
    }
}