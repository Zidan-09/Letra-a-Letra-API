import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";

export async function runFlow(context) {
    const [ws1, ws2] = context.sockets;

    const users = context.users;

    send(ws1, {
        type: "MATCHMAKING_GAME",
        gameMode: "NORMAL"
    });

    send(ws2, {
        type: "MATCHMAKING_GAME",
        gameMode: "NORMAL"
    });

    const started = await waitForEvent(e => (e.event === "MATCHMAKING_GAME" && e.status === "FOUNDED"), context.events);
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
            e => e.event === "GAME_OVER" ||
                (
                    e.event === "PLAYER_ACTION_RESULT" &&
                    e.data.currentTurnPlayerId !== currentPlayer
                ),
                context.events
        );

        if (result.event === "GAME_OVER") {
            gameRunning = false;
            break;
        }

        currentPlayer = result.data.currentTurnPlayerId;
    }
}