import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";

export async function runFlow(context) {
    async function init() {
        const [ws1, ws2] = context.sockets;

        const users = context.users;
        const events = context.events.get(users[0]);

        send(ws1, {
            type: "CREATE_GAME",
            name: "Test Casual",
            settings: {
                allowSpectators: false,
                privateGame: false
            }
        });

        const created = await waitForEvent("GAME_CREATED", e => (e.event === "GAME_CREATED"), events);

        await sleep(1000);

        const gameId = created.data.gameId;

        send(ws2, {
            type: "JOIN_GAME",
            gameId: gameId
        });

        await waitForEvent("PARTICIPANT_JOIN", e => (e.event === "PARTICIPANT_JOIN"), events);

        await sleep(1000);
    }

    await init();

    async function play() {
        send(ws1, {
            type: "START_GAME",
            gameId: gameId,
            settings: {
                themeId: "tech",
                gameMode: "NORMAL"
            }
        });

        const started = await waitForEvent("GAME_STARTED", e => (e.event === "GAME_STARTED"), events);

        await sleep(1000);

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
                "GAME_OVER / PLAYER_ACTION_RESULT",
                e => e.event === "GAME_OVER" ||
                    (
                        e.event === "PLAYER_ACTION_RESULT" &&
                        e.data.currentTurnPlayerId !== currentPlayer
                    ),
                    events
            );

            await sleep(125);

            if (result.event === "GAME_OVER") {
                gameRunning = false;
                break;
            }

            currentPlayer = result.data.currentTurnPlayerId;
        }
    }

    await play();

    await sleep(1000);

    await play();

    await sleep(1000);

    await play();

    await sleep(1000);

    await play();
}