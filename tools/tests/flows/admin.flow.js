import { http } from "../core/http.js";
import { download } from "../core/download.js";

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

    // Fluxo 1: Buscar perfil do administrador autenticado

    res = await http(
        "GET",
        "/admin/me",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get admin profile"
    );

    if (res.body.data.admin.email !== admin.email) {
        throw new Error(
            `Get admin profile: expected ${admin.email}, received ${res.data.data.email}`
        );
    }

    res = await http(
        "GET",
        "/admin/logs/admin",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get admin logs list"
    )
    // Fluxo 2: Buscar logs de administração

    res = await download(
        "GET",
        "/admin/logs/admin/latest.log",
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get admin latest log"
    );

    if (!res.data) {
        throw new Error(
            "Get admin logs: empty response"
        );
    }

    // Fluxo 3: Buscar datas dos logs de partidas

    res = await http(
        "GET",
        "/admin/logs/game",
        null,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get game log dates"
    );

    if (!Array.isArray(res.body)) {
        throw new Error(
            "Get game log dates: invalid response"
        );
    }

    // Fluxo 4: Buscar partidas de uma data

    if (res.body.length > 0) {

        const date = res.body[0];

        res = await http(
            "GET",
            `/admin/logs/game/${date}`,
            null,
            admin.token
        );

        ensureStatus(
            res,
            200,
            "Get game logs by date"
        );

        if (!Array.isArray(res.body)) {
            throw new Error(
                "Get game logs by date: invalid response"
            );
        }

        // Fluxo 5: Buscar arquivos de uma partida

        if (res.body.length > 0) {

            const gameId = res.body[0];

            res = await http(
                "GET",
                `/admin/logs/game/${date}/${gameId}`,
                null,
                admin.token
            );

            ensureStatus(
                res,
                200,
                "Get game log files"
            );

            if (!Array.isArray(res.body)) {
                throw new Error(
                    "Get game log files: invalid response"
                );
            }

            // Fluxo 6: Baixar um arquivo da partida

            if (res.body.length > 0) {

                const file = res.body[0];

                res = await download(
                    "GET",
                    `/admin/logs/game/${date}/${gameId}/${file}`,
                    admin.token
                );

                ensureStatus(
                    res,
                    200,
                    "Download game log"
                );

                if (!res.data) {
                    throw new Error(
                        "Download game log: empty response"
                    );
                }
            }
        }
    }

    // Fluxo 7: Buscar arquivos untracked

    res = await http(
        "GET",
        "/admin/logs/game/untracked",
        null,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get untracked game logs"
    );

    if (!Array.isArray(res.body)) {
        throw new Error(
            "Get untracked game logs: invalid response"
        );
    }

    // Fluxo 8: Baixar um arquivo untracked

    if (res.body.length > 0) {

        const file = res.body[0];

        res = await download(
            "GET",
            `/admin/logs/game/untracked/${file}`,
            admin.token
        );

        ensureStatus(
            res,
            200,
            "Download untracked game log"
        );

        if (!res.data) {
            throw new Error(
                "Download untracked game log: empty response"
            );
        }
    }

    // Fluxo 8: Registrar novo administrador

    const newAdmin = {
        name: `integration.admin.${Date.now()}`,
        email: `integration.admin.${Date.now()}@localhost.com`,
        password: "12345678"
    };


    res = await http(
        "POST",
        "/admin",
        newAdmin,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register admin"
    );


    // Fluxo 9: Autenticar novo administrador

    res = await http(
        "POST",
        "/admin/auth",
        {
            email: newAdmin.email,
            password: newAdmin.password
        }
    );

    ensureStatus(
        res,
        200,
        "Auth registered admin"
    );


    if (!res.body.data.token) {
        throw new Error(
            "Auth registered admin: token not found"
        );
    }


    const newAdminToken = res.body.data.token;


    // Fluxo 10: Validar perfil do novo administrador

    res = await http(
        "GET",
        "/admin/me",
        undefined,
        newAdminToken
    );

    ensureStatus(
        res,
        200,
        "Get new admin profile"
    );

    if (res.body.data.admin.email !== newAdmin.email) {
        throw new Error(
            `Get new admin profile: expected ${newAdmin.email}, received ${res.data.data.email}`
        );
    }
}