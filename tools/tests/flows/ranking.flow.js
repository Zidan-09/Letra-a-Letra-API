import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";

export async function runRankingGame(context) {
    const [ws1, ws2] = context.sockets;

    const users = context.users;
    const events = context.events.get(users[0]);

    send(ws1, {
        type: "RANKING_GAME"
    });

    send(ws2, {
        type: "RANKING_GAME"
    });

    const started = await waitForEvent("RANKING_GAME", e => (e.event === "RANKING_GAME" && e.status === "FOUNDED"), events);
    await sleep(1000);
    const gameId = started.gameId;

    let currentPlayer = started.data.currentTurnPlayerId;

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
            "RANKING_OVER",
            e => e.event === "RANKING_OVER" ||
                (
                    e.event === "PLAYER_ACTION_RESULT" &&
                    e.data.currentTurnPlayerId !== currentPlayer
                ),
                events
        );

        await sleep(125);

        if (result.event === "RANKING_OVER") {
            gameRunning = false;
            break;
        }

        currentPlayer = result.data.currentTurnPlayerId;
    }
}