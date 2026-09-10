import { waitForEvent } from "../core/waitForEvent.js";
import { connect, send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";

function findPowerHolder(state) {
    for (const player of state?.players ?? []) {
        for (const item of player?.inventory ?? []) {
            if (item?.id) {
                return { userId: player.id, powerId: item.id };
            }
        }
    }

    return undefined;
}

export async function runFlow(context) {
    let [ws1, ws2] = context.sockets;
    const [user1, user2] = context.users;
    const events = context.getSharedEvents();

    const socketFor = userId => userId === user1.id ? ws1 : ws2;

    // Fluxo 1: Parear partida casual

    send(ws1, {
        type: "MATCHMAKING_GAME",
        gameMode: "NORMAL"
    });

    send(ws2, {
        type: "MATCHMAKING_GAME",
        gameMode: "NORMAL"
    });

    const started = await waitForEvent("MATCHMAKING_GAME", e => e.event === "MATCHMAKING_GAME" && e.status === "FOUNDED", events);

    await sleep(1000);

    const gameId = started.gameId;
    let currentPlayer = started.data.currentTurnPlayerId;
    let state = started.data;

    const positions = [];
    for (let x = 0; x < 10; x++) {
        for (let y = 0; y < 10; y++) {
            positions.push({ x, y });
        }
    }

    // Fluxo 2: Jogar até algum jogador receber um poder

    let holder;

    for (let i = 0; i < 40 && !holder; i++) {
        holder = findPowerHolder(state);

        if (holder) {
            break;
        }

        const pos = positions.shift();

        if (!pos) {
            break;
        }

        send(socketFor(currentPlayer), {
            type: "PLAYER_ACTION",
            gameId: gameId,
            action: {
                type: "REVEAL",
                position: pos
            }
        });

        const result = await waitForEvent(
            "PLAYER_ACTION_RESULT / GAME_OVER",
            e => e.event === "GAME_OVER" ||
                (
                    e.event === "PLAYER_ACTION_RESULT" &&
                    e.data.currentTurnPlayerId !== currentPlayer
                ),
            events
        );

        await sleep(250);

        if (result.event === "GAME_OVER") {
            throw new Error(
                "DISCARD_POWER: match ended before any power was granted"
            );
        }

        currentPlayer = result.data.currentTurnPlayerId;
        state = result.data;
    }

    holder = findPowerHolder(state);

    if (!holder) {
        throw new Error(
            "DISCARD_POWER: no power granted within 40 moves"
        );
    }

    // Fluxo 3: Descartar o poder em partida em andamento

    send(socketFor(holder.userId), {
        type: "DISCARD_POWER",
        gameId: gameId,
        powerId: holder.powerId
    });

    const discarded = await waitForEvent("POWER_DISCARDED", e => e.event === "POWER_DISCARDED", events);

    await sleep(500);

    const discardedState = discarded.data;

    if (!discardedState) {
        throw new Error(
            `POWER_DISCARDED: missing game state in ${JSON.stringify(discarded)}`
        );
    }

    const holderState = discardedState.players.find(player => player.id === holder.userId);

    if (!holderState || holderState.inventory.some(item => item.id === holder.powerId)) {
        throw new Error(
            "POWER_DISCARDED: power was not removed from inventory"
        );
    }

    currentPlayer = discardedState.currentTurnPlayerId;

    // Fluxo 4: Desconectar no meio da partida

    ws1.close();

    await waitForEvent("PARTICIPANT_DISCONNECTED", e => e.event === "PARTICIPANT_DISCONNECTED", events);

    await sleep(1000);

    // Fluxo 5: Reconectar na partida

    ws1 = await connect(user1, context.events.get(user1), event => context.addSharedEvent(event));
    context.sockets[0] = ws1;

    await waitForEvent("PARTICIPANT_RECONNECTED", e => e.event === "PARTICIPANT_RECONNECTED", events);

    await sleep(1000);

    // Fluxo 6: Continuar jogando após a reconexão (passagem de turno)

    const pos = positions.shift();

    if (!pos) {
        throw new Error(
            "Reconnect continuity: no positions left to play"
        );
    }

    send(socketFor(currentPlayer), {
        type: "PLAYER_ACTION",
        gameId: gameId,
        action: {
            type: "REVEAL",
            position: pos
        }
    });

    const moved = await waitForEvent(
        "PLAYER_ACTION_RESULT / GAME_OVER after reconnect",
        e => e.event === "GAME_OVER" ||
            (
                e.event === "PLAYER_ACTION_RESULT" &&
                e.data.currentTurnPlayerId !== currentPlayer
            ),
        events
    );

    await sleep(500);

    if (moved.event !== "GAME_OVER" && moved.data.currentTurnPlayerId === currentPlayer) {
        throw new Error(
            "Reconnect continuity: turn did not pass after reconnect"
        );
    }
}
