import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";

export async function runFlow(context) {
    const [ws1, ws2, ws3] = context.sockets;

    const users = context.users;
    const events = context.getSharedEvents();

    let gameId;

    async function init() {
        send(ws1, {
            type: "CREATE_GAME",
            name: "Test Casual",
            settings: {
                allowSpectators: true,
                privateGame: false
            }
        });

        const created = await waitForEvent("GAME_CREATED", e => (e.event ?? e.type) === "GAME_CREATED", events);

        await sleep(1000);

        gameId = created.data?.gameId ?? created.data?.roomId ?? created.data?.id ?? created.gameId ?? created.roomId;

        if (!gameId) {
            throw new Error(`casual init: gameId missing in ${JSON.stringify(created)}`);
        }

        send(ws2, {
            type: "JOIN_GAME",
            gameId: gameId
        });

        await waitForEvent("PARTICIPANT_JOIN", e => (e.event ?? e.type) === "PARTICIPANT_JOIN", events);

        send(ws3, {
            type: "JOIN_GAME",
            gameId: gameId
        });

        await waitForEvent("PARTICIPANT_JOIN", e => (e.event ?? e.type) === "PARTICIPANT_JOIN", events);

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

        const started = await waitForEvent("GAME_STARTED", e => (e.event ?? e.type) === "GAME_STARTED", events);

        await sleep(1000);

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

    for (let i = 0; i < 3; i++) {
        await play();

        context.clearEvents();

        await sleep(1000);
    }
}