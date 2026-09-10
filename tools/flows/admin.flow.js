import { http } from "../core/http.js";
import { download } from "../core/download.js";
import { connectAdmin } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";
import { waitForAdminActivationToken, waitForAdminResetToken } from "../core/mailhog.js";

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
    const [admin] = context.admins;

    const stamp = Date.now();

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

    // Fluxo 8: Registrar novo administrador

    const newAdmin = {
        name: `integration.admin.${Date.now()}`,
        email: `integration.admin.${Date.now()}@localhost.com`,
        password: "12345678"
    };


    res = await http(
        "POST",
        "/admin",
        {
            name: newAdmin.name,
            email: newAdmin.email
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register admin"
    );

    // Fluxo 8.5: Conceder permissões para o novo administrador

    const actions = ["VIEW", "CREATE", "EDIT", "DELETE", "TOGGLE"];
    const permissions = [
            {
                key: "USER",
                actions
            },
            {
                key: "LOGS",
                actions
            },
            {
                key: "ADMIN",
                actions
            },
            {
                key: "COSMETIC",
                actions
            },
            {
                key: "GAME",
                actions
            },
            {
                key: "LEVELS",
                actions
            },
            {
                key: "OFFERS",
                actions
            },
            {
                key: "TRANSACTIONS",
                actions
            }
        ]

    const updatedAdmin = {
        name: newAdmin.name,
        email: newAdmin.email,
        isSuper: true,
        permissions
    }

    res = await http(
        "PUT",
        "/admin/" + res.body.data.admin.id,
        updatedAdmin,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Update admin"
    );

    // Fluxo 8.6: Ativar a conta do novo administrador

    const activationToken = await waitForAdminActivationToken(newAdmin.email);

    res = await http(
        "PATCH",
        `/admin/activate?token=${encodeURIComponent(activationToken)}`,
        {
            password: newAdmin.password
        }
    );

    ensureStatus(
        res,
        200,
        "Activate registered admin"
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

    // Fluxo 11: Tornar o administrador padrão um Super admin

    const updatedDefaultAdmin = {
        name: admin.name,
        email: admin.email,
        isSuper: true,
        permissions
    }

    res = await http(
        "PUT",
        "/admin/" + admin.id,
        updatedDefaultAdmin,
        newAdminToken
    );

    ensureStatus(
        res,
        200,
        "Update Default admin"
    );

    // Fluxo 2: Buscar logs de administração

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

    // Fluxo 12: Listar administradores com paginação

    res = await http(
        "GET",
        "/admin?page=0&size=5&sort=email,asc",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get admins"
    );

    const adminsPage = res.body.data;

    if (!Array.isArray(adminsPage.content)) {
        throw new Error(
            "Get admins: invalid page content"
        );
    }

    if (!adminsPage.content.some(item => item.email === admin.email)) {
        throw new Error(
            "Get admins: authenticated admin not found"
        );
    }

    for (const field of ["page", "size", "totalElements", "totalPages", "first", "last"]) {
        if (adminsPage[field] === undefined) {
            throw new Error(
                `Get admins: missing page field ${field}`
            );
        }
    }

    // Fluxo 13: Buscar administrador por e-mail

    res = await http(
        "GET",
        `/admin/email/${encodeURIComponent(admin.email)}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Find admin by email"
    );

    if (res.body.data.admin.email !== admin.email) {
        throw new Error(
            `Find admin by email: expected ${admin.email}, received ${res.body.data.admin.email}`
        );
    }

    res = await http(
        "GET",
        `/admin/email/${encodeURIComponent(`unknown.admin.${stamp}@localhost.com`)}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        400,
        "Find unknown admin by email"
    );

    if (res.body?.code !== "ADMIN_NOT_FOUND") {
        throw new Error(
            `Find unknown admin by email: expected ADMIN_NOT_FOUND, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 14: Remover administrador secundário e tentativas inválidas

    const removableAdmin = {
        name: `integration.removable.${stamp}`,
        email: `integration.removable.${stamp}@localhost.com`,
        password: "12345678"
    };

    res = await http(
        "POST",
        "/admin",
        {
            name: removableAdmin.name,
            email: removableAdmin.email
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register removable admin"
    );

    const removableAdminId = res.body.data.admin.id;

    const removableActivationToken = await waitForAdminActivationToken(removableAdmin.email);

    res = await http(
        "PATCH",
        `/admin/activate?token=${encodeURIComponent(removableActivationToken)}`,
        {
            password: removableAdmin.password
        }
    );

    ensureStatus(
        res,
        200,
        "Activate removable admin"
    );

    res = await http(
        "DELETE",
        `/admin/${removableAdminId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Delete secondary admin"
    );

    if (res.body.data.admin.id !== removableAdminId) {
        throw new Error(
            "Delete secondary admin: id mismatch"
        );
    }

    res = await http(
        "GET",
        `/admin/email/${encodeURIComponent(removableAdmin.email)}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        400,
        "Find deleted admin by email"
    );

    if (res.body?.code !== "ADMIN_NOT_FOUND") {
        throw new Error(
            `Find deleted admin by email: expected ADMIN_NOT_FOUND, received ${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "DELETE",
        `/admin/${admin.id}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        400,
        "Delete own admin"
    );

    if (res.body?.code !== "INVALID_ADMIN_OPERATION") {
        throw new Error(
            `Delete own admin: expected INVALID_ADMIN_OPERATION, received ${JSON.stringify(res.body)}`
        );
    }

    const regularAdmin = {
        name: `integration.regular.${stamp}`,
        email: `integration.regular.${stamp}@localhost.com`,
        password: "12345678"
    };

    res = await http(
        "POST",
        "/admin",
        {
            name: regularAdmin.name,
            email: regularAdmin.email
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register regular admin"
    );

    const regularAdminId = res.body.data.admin.id;

    const regularActivationToken = await waitForAdminActivationToken(regularAdmin.email);

    res = await http(
        "PATCH",
        `/admin/activate?token=${encodeURIComponent(regularActivationToken)}`,
        {
            password: regularAdmin.password
        }
    );

    ensureStatus(
        res,
        200,
        "Activate regular admin"
    );

    res = await http(
        "POST",
        "/admin/auth",
        {
            email: regularAdmin.email,
            password: regularAdmin.password
        }
    );

    ensureStatus(
        res,
        200,
        "Auth regular admin"
    );

    const regularAdminToken = res.body.data.token;

    res = await http(
        "PUT",
        "/admin/" + regularAdminId,
        {
            name: regularAdmin.name,
            email: regularAdmin.email,
            isSuper: false,
            permissions: [
                { key: "ADMIN", actions }
            ]
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Grant admin permission to regular admin"
    );

    res = await http(
        "DELETE",
        `/admin/${admin.id}`,
        undefined,
        regularAdminToken
    );

    ensureStatus(
        res,
        400,
        "Delete super admin as regular admin"
    );

    if (res.body?.code !== "PERMISSION_DENIED") {
        throw new Error(
            `Delete super admin as regular admin: expected PERMISSION_DENIED, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 15: Solicitar redefinição de senha

    res = await http(
        "POST",
        "/admin/auth/forgot-password",
        {
            email: admin.email
        }
    );

    ensureStatus(
        res,
        204,
        "Forgot admin password"
    );

    res = await http(
        "POST",
        "/admin/auth/forgot-password",
        {
            email: `unknown.admin.${stamp}@localhost.com`
        }
    );

    ensureStatus(
        res,
        204,
        "Forgot admin password with unknown email"
    );

    res = await http(
        "POST",
        "/admin/auth/forgot-password",
        {
            email: "not-an-email"
        }
    );

    ensureStatus(
        res,
        400,
        "Forgot admin password with invalid email"
    );

    // Fluxo 16: Verificar token de redefinição

    const resetToken = await waitForAdminResetToken(admin.email);

    res = await http(
        "POST",
        "/admin/auth/verify-reset-token",
        {
            email: admin.email,
            token: resetToken
        }
    );

    ensureStatus(
        res,
        204,
        "Verify valid reset token"
    );

    res = await http(
        "POST",
        "/admin/auth/verify-reset-token",
        {
            email: admin.email,
            token: "00000000-0000-0000-0000-000000000000"
        }
    );

    ensureStatus(
        res,
        400,
        "Verify invalid reset token"
    );

    if (res.body?.code !== "INVALID_TOKEN") {
        throw new Error(
            `Verify invalid reset token: expected INVALID_TOKEN, received ${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "POST",
        "/admin/auth/forgot-password",
        {
            email: admin.email
        }
    );

    ensureStatus(
        res,
        204,
        "Forgot admin password again"
    );

    await waitForAdminResetToken(admin.email);

    res = await http(
        "POST",
        "/admin/auth/verify-reset-token",
        {
            email: admin.email,
            token: resetToken
        }
    );

    ensureStatus(
        res,
        400,
        "Verify superseded reset token"
    );

    if (res.body?.code !== "INVALID_TOKEN") {
        throw new Error(
            `Verify superseded reset token: expected INVALID_TOKEN, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 17: Redefinir senha com token válido e reutilização

    const passwordAdmin = {
        name: `integration.password.${stamp}`,
        email: `integration.password.${stamp}@localhost.com`,
        password: "12345678"
    };

    res = await http(
        "POST",
        "/admin",
        {
            name: passwordAdmin.name,
            email: passwordAdmin.email
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register password admin"
    );

    const passwordActivationToken = await waitForAdminActivationToken(passwordAdmin.email);

    res = await http(
        "PATCH",
        `/admin/activate?token=${encodeURIComponent(passwordActivationToken)}`,
        {
            password: passwordAdmin.password
        }
    );

    ensureStatus(
        res,
        200,
        "Activate password admin"
    );

    res = await http(
        "POST",
        "/admin/auth/forgot-password",
        {
            email: passwordAdmin.email
        }
    );

    ensureStatus(
        res,
        204,
        "Forgot password admin password"
    );

    const passwordResetToken = await waitForAdminResetToken(passwordAdmin.email);

    const newPassword = "87654321";

    res = await http(
        "POST",
        "/admin/auth/reset-password",
        {
            email: passwordAdmin.email,
            newPassword,
            token: passwordResetToken
        }
    );

    ensureStatus(
        res,
        204,
        "Reset admin password"
    );

    res = await http(
        "POST",
        "/admin/auth",
        {
            email: passwordAdmin.email,
            password: newPassword
        }
    );

    ensureStatus(
        res,
        200,
        "Auth admin with new password"
    );

    if (!res.body.data.token) {
        throw new Error(
            "Auth admin with new password: token not found"
        );
    }

    res = await http(
        "POST",
        "/admin/auth/reset-password",
        {
            email: passwordAdmin.email,
            newPassword: "12345678",
            token: passwordResetToken
        }
    );

    ensureStatus(
        res,
        400,
        "Reuse reset token"
    );

    if (res.body?.code !== "INVALID_TOKEN") {
        throw new Error(
            `Reuse reset token: expected INVALID_TOKEN, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 18: Ativar conta com token válido e inválido

    const activationAdmin = {
        name: `integration.activation.${stamp}`,
        email: `integration.activation.${stamp}@localhost.com`,
        password: "12345678"
    };

    res = await http(
        "POST",
        "/admin",
        {
            name: activationAdmin.name,
            email: activationAdmin.email
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register activation admin"
    );

    const setupToken = await waitForAdminActivationToken(activationAdmin.email);

    res = await http(
        "PATCH",
        `/admin/activate?token=${encodeURIComponent(setupToken)}`,
        {
            password: activationAdmin.password
        }
    );

    ensureStatus(
        res,
        200,
        "Activate account with valid token"
    );

    res = await http(
        "POST",
        "/admin/auth",
        {
            email: activationAdmin.email,
            password: activationAdmin.password
        }
    );

    ensureStatus(
        res,
        200,
        "Auth activated admin"
    );

    res = await http(
        "PATCH",
        `/admin/activate?token=${encodeURIComponent("00000000-0000-0000-0000-000000000000")}`,
        {
            password: activationAdmin.password
        }
    );

    ensureStatus(
        res,
        400,
        "Activate account with invalid token"
    );

    if (res.body?.code !== "INVALID_TOKEN") {
        throw new Error(
            `Activate account with invalid token: expected INVALID_TOKEN, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 19: Conectar no WebSocket administrativo e receber pushes

    const { ws: adminWs, messages: adminWsMessages } = await connectAdmin(admin.token);

    try {
        const metrics = await waitForAdminEvent(adminWsMessages, "METRICS", 15000);

        if (!metrics.system || !metrics.application) {
            throw new Error(
                `Admin METRICS push: missing payload in ${JSON.stringify(metrics)}`
            );
        }

        res = await http(
            "GET",
            "/admin/me",
            undefined,
            admin.token
        );

        ensureStatus(
            res,
            200,
            "Get admin profile for log push"
        );

        const log = await waitForAdminEvent(adminWsMessages, "LOG", 15000);

        if (typeof log.log !== "string" || log.log.length === 0) {
            throw new Error(
                `Admin LOG push: missing log in ${JSON.stringify(log)}`
            );
        }
    } finally {
        try { adminWs.close(); } catch {}
    }
}

async function waitForAdminEvent(messages, event, timeoutMs) {
    const deadline = Date.now() + timeoutMs;

    while (Date.now() < deadline) {
        const found = messages.find(message => message?.event === event);

        if (found) {
            return found;
        }

        await sleep(250);
    }

    throw new Error(
        `Admin WebSocket: event ${event} not received`
    );
}