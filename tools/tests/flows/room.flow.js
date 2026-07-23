import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";

export async function runFlow(context) {
    const [ws1, ws2, ws3] = context.sockets;

    const users = context.users;

    send(ws1, {
        type: "CREATE_GAME",
        name: "Test Room",
        settings: {
            allowSpectators: true,
            privateGame: false
        }
    });

    const created = await waitForEvent(e => (e.event === "GAME_CREATED"), context.events);

    const gameId = created.data.gameId;

    send(ws2, {
        type: "JOIN_GAME",
        gameId: gameId
    });

    await waitForEvent(e => (e.event === "PARTICIPANT_JOIN"), context.events);

    send(ws3, {
        type: "JOIN_GAME",
        gameId: gameId
    });

    await waitForEvent(e => (e.event === "PARTICIPANT_JOIN"), context.events);

    send(ws2, {
        type: "SWAP_POSITION",
        gameId: gameId,
        position: 3
    });

    await waitForEvent(e => (e.event === "POSITIONS_UPDATED"), context.events);

    send(ws3, {
        type: "SWAP_POSITION",
        gameId: gameId,
        position: 1
    });

    await waitForEvent(e => (e.event === "POSITIONS_UPDATED"), context.events);

    send(ws1, {
        type: "KICK_PARTICIPANT",
        gameId: gameId,
        participantId: users[2].id
    });

    await waitForEvent(e => (e.event === "PARTICIPANT_KICKED"), context.events);

    send(ws1, {
        type: "BAN_PARTICIPANT",
        gameId: gameId,
        participantId: users[1].id
    });

    await waitForEvent(e => (e.event === "PARTICIPANT_BANNED"), context.events);

    send(ws1, {
        type: "UNBAN_PARTICIPANT",
        gameId: gameId,
        userId: users[1].id
    });

    await waitForEvent(e => (e.event === "PARTICIPANT_UNBANNED"), context.events);

    send(ws2, {
        type: "JOIN_GAME",
        gameId: gameId
    });

    await waitForEvent(e => (e.event === "PARTICIPANT_JOIN"), context.events);

    send(ws1, {
        type: "LEFT_GAME",
        gameId: gameId
    });

    await waitForEvent(e => (e.event === "PARTICIPANT_LEAVE"), context.events);

    send(ws3, {
        type: "JOIN_GAME",
        gameId: gameId
    });

    await waitForEvent(e => (e.event === "PARTICIPANT_JOIN"), context.events);

    send(ws2, {
        type: "START_GAME",
        gameId: gameId,
        settings: {
            themeId: "tech",
            gameMode: "NORMAL"
        }
    });

    await waitForEvent(e => (e.event === "GAME_STARTED"), context.events);
}