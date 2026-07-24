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

    // Fluxo 2: Buscar logs de administração

    res = await download(
        "GET",
        "/admin/logs/admin/latest.log",
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get admin logs"
    );

    if (!res.data) {
        throw new Error(
            "Get admin logs: empty response"
        );
    }

    // Fluxo 3: Registrar novo administrador

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


    // Fluxo 4: Autenticar novo administrador

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


    // Fluxo 5: Validar perfil do novo administrador

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