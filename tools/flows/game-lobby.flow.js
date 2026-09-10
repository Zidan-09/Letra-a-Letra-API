import { http } from "../core/http.js";
import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";

function ensureStatus(response, expected, operation) {
    const expectedStatus = Array.isArray(expected)
        ? expected
        : [expected];

    if (!expectedStatus.includes(response.status)) {
        throw new Error(
            `${operation}: expected ${expectedStatus.join(" or ")}, received ${response.status} =-=-= ${JSON.stringify(response.body)}`
        );
    }
}

export async function runFlow(context) {
    const [user] = context.users;
    const [ws] = context.sockets;
    const events = context.getSharedEvents();

    const stamp = Date.now();

    let res;

    // Fluxo 1: Criar sala pública via WS

    send(ws, {
        type: "CREATE_GAME",
        name: `Lobby ${stamp}`,
        settings: {
            allowSpectators: true,
            privateGame: false
        }
    });

    const created = await waitForEvent("GAME_CREATED", e => e.event === "GAME_CREATED", events);

    await sleep(1000);

    const gameId = created.data?.gameId;

    if (!gameId) {
        throw new Error(
            `Create public room: missing gameId in ${JSON.stringify(created)}`
        );
    }

    // Fluxo 2: Listar partidas públicas e conferir a sala criada

    res = await http(
        "GET",
        "/game/public?page=0&size=10",
        undefined,
        user.token
    );

    ensureStatus(
        res,
        200,
        "Get public games"
    );

    if (!res.body.data.content.some(game => game.gameId === gameId)) {
        throw new Error(
            "Get public games: created room not found"
        );
    }

    for (const field of ["page", "size", "totalElements", "totalPages", "first", "last"]) {
        if (res.body.data[field] === undefined) {
            throw new Error(
                `Get public games: missing page field ${field}`
            );
        }
    }

    // Fluxo 3: Buscar sala por código inexistente

    res = await http(
        "GET",
        "/game/code/ZZZZ99",
        undefined,
        user.token
    );

    ensureStatus(
        res,
        400,
        "Find game by unknown code"
    );

    if (res.body?.code !== "GAME_NOT_FOUND") {
        throw new Error(
            `Find game by unknown code: expected GAME_NOT_FOUND, received ${JSON.stringify(res.body)}`
        );
    }
}
