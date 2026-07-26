import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";

export async function runFlow(context) {
    const [ws1, ws2, ws3] = context.sockets;

    const users = context.users;
    const events = context.events.get(users[0]);

    send(ws1, {
        type: "CREATE_GAME",
        name: "Test Room",
        settings: {
            allowSpectators: true,
            privateGame: false
        }
    });

    const created = await waitForEvent("GAME_CREATED", e => (e.event === "GAME_CREATED"), events);

    const gameId = created.data.gameId;

    send(ws2, {
        type: "JOIN_GAME",
        gameId: gameId
    });

    await waitForEvent("PARTICIPANT_JOIN", e => (e.event === "PARTICIPANT_JOIN"), events);

    send(ws3, {
        type: "JOIN_GAME",
        gameId: gameId
    });

    await waitForEvent("PARTICIPANT_JOIN", e => (e.event === "PARTICIPANT_JOIN"), events);

    send(ws2, {
        type: "SWAP_POSITION",
        gameId: gameId,
        position: 3
    });

    await waitForEvent("POSITIONS_UPDATED", e => (e.event === "POSITIONS_UPDATED"), events);

    send(ws3, {
        type: "SWAP_POSITION",
        gameId: gameId,
        position: 1
    });

    await waitForEvent("POSITIONS_UPDATED", e => (e.event === "POSITIONS_UPDATED"), events);

    send(ws1, {
        type: "KICK_PARTICIPANT",
        gameId: gameId,
        participantId: users[2].id
    });

    await waitForEvent("PARTICIPANT_KICKED", e => (e.event === "PARTICIPANT_KICKED"), events);

    send(ws1, {
        type: "BAN_PARTICIPANT",
        gameId: gameId,
        participantId: users[1].id
    });

    await waitForEvent("PARTICIPANT_BANNED", e => (e.event === "PARTICIPANT_BANNED"), events);

    send(ws1, {
        type: "UNBAN_PARTICIPANT",
        gameId: gameId,
        userId: users[1].id
    });

    await waitForEvent("PARTICIPANT_UNBANNED", e => (e.event === "PARTICIPANT_UNBANNED"), events);

    send(ws2, {
        type: "JOIN_GAME",
        gameId: gameId
    });

    await waitForEvent("PARTICIPANT_JOIN", e => (e.event === "PARTICIPANT_JOIN"), events);

    send(ws1, {
        type: "LEFT_GAME",
        gameId: gameId
    });

    await waitForEvent("PARTICIPANT_LEAVE", e => (e.event === "PARTICIPANT_LEAVE"), context.events.get(users[1]));

    send(ws3, {
        type: "JOIN_GAME",
        gameId: gameId
    });

    await waitForEvent("PARTICIPANT_JOIN", e => (e.event === "PARTICIPANT_JOIN"), context.events.get(users[1]));

    send(ws2, {
        type: "START_GAME",
        gameId: gameId,
        settings: {
            themeId: "tech",
            gameMode: "NORMAL"
        }
    });

    await waitForEvent("GAME_STARTED", e => (e.event === "GAME_STARTED"), context.events.get(users[1]));
}