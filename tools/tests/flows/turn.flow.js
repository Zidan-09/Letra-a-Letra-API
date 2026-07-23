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

    const started = await waitForEvent(
        e => e.event === "MATCHMAKING_GAME" && e.status === "FOUNDED",
        context.events
    );

    const gameId = started.gameId;
    let currentPlayer = started.data.currentTurnPlayerId;

    const positions = [];
    for (let x = 0; x < 10; x++) {
        for (let y = 0; y < 10; y++) {
            positions.push({ x, y });
        }
    }

    const players = {
        1: users.find(u => u.id === currentPlayer),
        2: users.find(u => u.id !== currentPlayer)
    };

    const sockets = {
        [users[0].id]: ws1,
        [users[1].id]: ws2
    };

    const script = [
        { player: 1, action: "PLAY" },
        { player: 2, action: "PLAY" },
        { player: 1, action: "PASS" },
        { player: 2, action: "PLAY" },
        { player: 1, action: "PLAY" },
        { player: 2, action: "PASS" },
        { player: 1, action: "PLAY" },
        { player: 2, action: "PLAY" },
        { player: 1, action: "PASS" },
        { player: 2, action: "PLAY" },
        { player: 1, action: "PASS" },
        { player: 2, action: "PASS" },
        { player: 1, action: "PASS" }
    ];

    for (const [i, step] of script.entries()) {

        const expectedPlayer = players[step.player];

        if (currentPlayer !== expectedPlayer.id) {
            throw new Error(
                `Step ${i + 1}: expected turn of ${expectedPlayer.nickname}, but received ${currentPlayer}`
            );
        }

        if (step.action === "PLAY") {
            const pos = positions.splice(
                Math.floor(Math.random() * positions.length),
                1
            )[0];

            const currentWs = sockets[currentPlayer];

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
                break;
            }

            currentPlayer = result.data.currentTurnPlayerId;

        } else {

            if (i === script.length - 1) {
                await waitForEvent(
                    e => e.event === "TURN_EXPIRED",
                    context.events,
                    60000
                );

                await waitForEvent(
                    e => e.event === "GAME_OVER",
                    context.events
                );

                break;
            }

            const expired = await waitForEvent(
                e => e.event === "TURN_EXPIRED",
                context.events,
                60000
            );

            currentPlayer = expired.data.currentTurnPlayerId;
        }
    }
}