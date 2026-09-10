import { http } from "../core/http.js";
import { multipart } from "../core/multipart.js";

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

// PNG 1x1 válido: o backend converte o asset para WebP via ImageIO,
// então o upload precisa de bytes de imagem decodificáveis (não texto).
const PNG_1X1_BASE64 =
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";

function imageAsset(filename) {
    return {
        blob: new Blob(
            [Buffer.from(PNG_1X1_BASE64, "base64")],
            {
                type: "image/png"
            }
        ),
        filename
    };
}

// O perfil `test` fixa o código em `LocalPasswordResetCodeService`
// para permitir a cobertura E2E do fluxo de reset.
const TEST_RESET_CODE = "123456";

async function getNickname(user) {
    const res = await http(
        "GET",
        "/user/me",
        undefined,
        user.token
    );

    ensureStatus(
        res,
        200,
        "Get nickname"
    );

    return res.body.data.user.nickname;
}

export async function runFlow(adminContext, playerContext) {
    const [admin] = adminContext.admins;
    const [mainUser, secondUser, banUser, resetUser] = playerContext.users;

    const stamp = Date.now();

    let res;

    // Fluxo 2: Solicitar código de reset de senha

    res = await http(
        "POST",
        "/user/auth/forgot-password",
        {
            email: resetUser.email
        }
    );

    ensureStatus(
        res,
        204,
        "Forgot user password"
    );

    res = await http(
        "POST",
        "/user/auth/forgot-password",
        {
            email: `unknown.user.${stamp}@email.com`
        }
    );

    ensureStatus(
        res,
        204,
        "Forgot user password with unknown email"
    );

    // Fluxo 3: Verificar código de reset válido e inválido

    res = await http(
        "POST",
        "/user/auth/verify-reset-code",
        {
            email: resetUser.email,
            code: TEST_RESET_CODE
        }
    );

    ensureStatus(
        res,
        204,
        "Verify valid reset code"
    );

    res = await http(
        "POST",
        "/user/auth/verify-reset-code",
        {
            email: resetUser.email,
            code: "000000"
        }
    );

    ensureStatus(
        res,
        400,
        "Verify invalid reset code"
    );

    if (res.body?.code !== "INVALID_TOKEN") {
        throw new Error(
            `Verify invalid reset code: expected INVALID_TOKEN, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 4: Redefinir senha e reutilização do código

    const resetNewPassword = "87654321";

    res = await http(
        "POST",
        "/user/auth/reset-password",
        {
            email: resetUser.email,
            newPassword: resetNewPassword,
            code: TEST_RESET_CODE
        }
    );

    ensureStatus(
        res,
        204,
        "Reset user password"
    );

    resetUser.password = resetNewPassword;

    res = await http(
        "POST",
        "/user/auth",
        {
            email: resetUser.email,
            password: resetNewPassword
        }
    );

    ensureStatus(
        res,
        200,
        "Auth user with new password"
    );

    if (!res.body.data.token) {
        throw new Error(
            "Auth user with new password: token not found"
        );
    }

    res = await http(
        "POST",
        "/user/auth/reset-password",
        {
            email: resetUser.email,
            newPassword: "12345678",
            code: TEST_RESET_CODE
        }
    );

    ensureStatus(
        res,
        400,
        "Reuse reset code"
    );

    if (res.body?.code !== "INVALID_TOKEN") {
        throw new Error(
            `Reuse reset code: expected INVALID_TOKEN, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 5: Listar usuários como admin após cadastros

    res = await http(
        "GET",
        "/user?page=0&size=10",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get users"
    );

    for (const user of [mainUser, secondUser, banUser, resetUser]) {
        if (!res.body.data.content.some(item => item.email === user.email)) {
            throw new Error(
                `Get users: ${user.email} not found`
            );
        }
    }

    // Fluxo 6: Perfil do usuário autenticado

    const mainNickname = await getNickname(mainUser);
    const secondNickname = await getNickname(secondUser);

    res = await http(
        "GET",
        "/user/me",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get my profile"
    );

    if (res.body.data.user.email !== mainUser.email) {
        throw new Error(
            `Get my profile: expected ${mainUser.email}, received ${res.body.data.user.email}`
        );
    }

    // Fluxo 7: Buscar usuário por username existente e inexistente

    res = await http(
        "GET",
        `/user/username/${encodeURIComponent(mainNickname)}`,
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Find user by username"
    );

    if (res.body.data.user.nickname !== mainNickname) {
        throw new Error(
            "Find user by username: nickname mismatch"
        );
    }

    res = await http(
        "GET",
        `/user/username/${encodeURIComponent(`unknown-${stamp}`)}`,
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        400,
        "Find unknown user by username"
    );

    if (res.body?.code !== "USER_NOT_FOUND") {
        throw new Error(
            `Find unknown user by username: expected USER_NOT_FOUND, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 8: Cadastrar cosmético, conceder ao usuário e ver inventário próprio

    const asset = imageAsset("avatar.png");
    const cosmeticName = `integration-user-${stamp}`;

    const form = new FormData();

    form.append("name", cosmeticName);
    form.append("cosmeticType", "AVATAR");
    form.append("asset", asset.blob, asset.filename);

    res = await multipart(
        "POST",
        "/cosmetic",
        form,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register cosmetic"
    );

    const cosmeticId = res.body?.data?.cosmetic?.id;

    if (!cosmeticId) {
        throw new Error(
            `Register cosmetic: missing id in response body=${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "PATCH",
        `/user/${mainUser.id}/grant-reward`,
        {
            rewardType: "COSMETIC",
            quantity: 1,
            rewardReference: cosmeticId
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Grant cosmetic reward"
    );

    res = await http(
        "GET",
        "/user/inventory",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get my inventory"
    );

    if (!res.body.data.inventory.some(item => item.cosmeticId === cosmeticId)) {
        throw new Error(
            "Get my inventory: granted cosmetic not found"
        );
    }

    // Fluxo 9: Inventário de outro usuário (visão admin)

    res = await http(
        "GET",
        `/user/${mainUser.id}/inventory?page=0&size=10`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get user inventory"
    );

    if (!res.body.data.content.some(item => item.cosmeticId === cosmeticId)) {
        throw new Error(
            "Get user inventory: granted cosmetic not found"
        );
    }

    // Fluxo 10: Conceder moedas e conferir extrato próprio

    res = await http(
        "PATCH",
        `/user/${mainUser.id}/grant-reward`,
        {
            rewardType: "COIN",
            quantity: 1000,
            rewardReference: null
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Grant coin reward"
    );

    res = await http(
        "GET",
        "/user/transactions?page=0&size=20",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get my transactions"
    );

    if (!res.body.data.content.some(item => item.reason === "ADMIN_GIVE" && item.operation === "CREDIT")) {
        throw new Error(
            "Get my transactions: grant transaction not found"
        );
    }

    // Fluxo 11: Troca de nickname válido, em uso e inválido

    res = await http(
        "PATCH",
        "/user/nickname",
        {
            nickname: "ab"
        },
        mainUser.token
    );

    ensureStatus(
        res,
        400,
        "Change nickname with invalid nickname"
    );

    res = await http(
        "PATCH",
        "/user/nickname",
        {
            nickname: secondNickname
        },
        mainUser.token
    );

    ensureStatus(
        res,
        400,
        "Change nickname already in use"
    );

    if (res.body?.code !== "NICKNAME_ALREADY_IN_USE") {
        throw new Error(
            `Change nickname already in use: expected NICKNAME_ALREADY_IN_USE, received ${JSON.stringify(res.body)}`
        );
    }

    const renamedNickname = `renamed${stamp % 100000}`;

    res = await http(
        "PATCH",
        "/user/nickname",
        {
            nickname: renamedNickname
        },
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Change nickname"
    );

    if (res.body.data.nickname !== renamedNickname) {
        throw new Error(
            "Change nickname: nickname mismatch"
        );
    }

    res = await http(
        "GET",
        `/user/username/${encodeURIComponent(renamedNickname)}`,
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Find user by new nickname"
    );

    res = await http(
        "PATCH",
        "/user/nickname",
        {
            nickname: `another${stamp % 100000}`
        },
        mainUser.token
    );

    ensureStatus(
        res,
        400,
        "Change nickname twice"
    );

    if (res.body?.code !== "USER_CANNOT_CHANGE_NICKNAME") {
        throw new Error(
            `Change nickname twice: expected USER_CANNOT_CHANGE_NICKNAME, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 12: Equipar cosmético possuído e não possuído

    res = await http(
        "PATCH",
        `/user/cosmetic/${cosmeticId}`,
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Equip owned cosmetic"
    );

    const equipped = res.body.data.inventoryItems.find(item => item.cosmeticId === cosmeticId);

    if (!equipped || equipped.equipped !== true) {
        throw new Error(
            "Equip owned cosmetic: cosmetic was not equipped"
        );
    }

    res = await http(
        "PATCH",
        "/user/cosmetic/00000000-0000-0000-0000-000000000000",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        400,
        "Equip unowned cosmetic"
    );

    if (res.body?.code !== "INVALID_COSMETIC") {
        throw new Error(
            `Equip unowned cosmetic: expected INVALID_COSMETIC, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 13: Banir usuário e bloquear login

    res = await http(
        "PATCH",
        `/user/${banUser.id}/ban`,
        {
            type: "PERMANENT",
            expiresIn: 0,
            reason: "Integration test ban"
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Ban user"
    );

    res = await http(
        "POST",
        "/user/auth",
        {
            email: banUser.email,
            password: banUser.password
        }
    );

    ensureStatus(
        res,
        400,
        "Auth banned user"
    );

    if (res.body?.code !== "USER_BANNED_FROM_GAME") {
        throw new Error(
            `Auth banned user: expected USER_BANNED_FROM_GAME, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 14: Reverter ban e voltar a autenticar

    res = await http(
        "PATCH",
        `/user/${banUser.id}/unban`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Unban user"
    );

    res = await http(
        "POST",
        "/user/auth",
        {
            email: banUser.email,
            password: banUser.password
        }
    );

    ensureStatus(
        res,
        200,
        "Auth unbanned user"
    );

    if (!res.body.data.token) {
        throw new Error(
            "Auth unbanned user: token not found"
        );
    }

    // Fluxo 15: Conceder moedas/gemas e conferir extrato

    res = await http(
        "PATCH",
        `/user/${mainUser.id}/grant-reward`,
        {
            rewardType: "COIN",
            quantity: 500,
            rewardReference: null
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Grant coins"
    );

    res = await http(
        "PATCH",
        `/user/${mainUser.id}/grant-reward`,
        {
            rewardType: "GEMS",
            quantity: 50,
            rewardReference: null
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Grant gems"
    );

    res = await http(
        "GET",
        "/user/transactions?page=0&size=20",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get transactions after grants"
    );

    if (!res.body.data.content.some(item => item.reason === "ADMIN_GIVE" && item.amount === 500)) {
        throw new Error(
            "Get transactions after grants: coin grant not found"
        );
    }

    // Fluxo 16: Revogar saldo e conferir extrato

    res = await http(
        "GET",
        "/user/me",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get balance before revoke"
    );

    const coinsBefore = res.body.data.user.wallet.coins;

    res = await http(
        "PATCH",
        `/user/${mainUser.id}/wallet/revoke`,
        {
            type: "SOFT",
            amount: 200
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Revoke wallet"
    );

    res = await http(
        "GET",
        "/user/me",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get balance after revoke"
    );

    if (res.body.data.user.wallet.coins !== coinsBefore - 200) {
        throw new Error(
            "Revoke wallet: balance was not reduced by 200"
        );
    }

    res = await http(
        "GET",
        "/user/transactions?page=0&size=20",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get transactions after revoke"
    );

    if (!res.body.data.content.some(item => item.reason === "ADMIN_REVOKE" && item.operation === "DEBIT" && item.amount === 200)) {
        throw new Error(
            "Get transactions after revoke: revoke transaction not found"
        );
    }

    // Fluxo 17: Revogar cosmético e conferir inventário

    res = await http(
        "DELETE",
        `/user/${mainUser.id}/inventory/${cosmeticId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Revoke cosmetic"
    );

    res = await http(
        "GET",
        "/user/inventory",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get inventory after revoke"
    );

    if (res.body.data.inventory.some(item => item.cosmeticId === cosmeticId)) {
        throw new Error(
            "Get inventory after revoke: revoked cosmetic still present"
        );
    }
}
