import { http } from "../core/http.js";

function ensureStatus(response, expected, operation) {
    const expectedStatus = Array.isArray(expected)
        ? expected
        : [expected];

    if (!expectedStatus.includes(response.status)) {
        throw new Error(
            `${operation}: expected ${expectedStatus.join(" or ")}, received ${response.status}`
        );
    }
}

export async function runFlow(context) {
    const [admin] = context.admins;

    let res;

    // Fluxo 1: Buscar todas as partidas

    res = await http(
        "GET",
        "/game?page=0&size=10",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get games"
    );

    if (!res.body.data.content) {
        throw new Error(
            "Get games: invalid page response"
        );
    }

    // Fluxo 2: Buscar partidas ativas

    res = await http(
        "GET",
        "/game/active?page=0&size=10",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get active games"
    );

    if (!res.body.data.content) {
        throw new Error(
            "Get active games: invalid page response"
        );
    }

    // Fluxo 3: Validar consistência da paginação

    const page = res.body.data;

    if (
        page.page < 0 ||
        page.size <= 0 ||
        page.totalElements < 0 ||
        page.totalPages < 0
    ) {
        throw new Error(
            "Get active games: invalid pagination"
        );
    }
}