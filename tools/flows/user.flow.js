import { http } from "../core/http.js";
import { registerItem } from "../core/item.js";

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

    if (res.body?.code !== "INVALID_RESET_CODE") {
        throw new Error(
            `Verify invalid reset code: expected INVALID_RESET_CODE, received ${JSON.stringify(res.body)}`
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

    if (res.body?.code !== "INVALID_RESET_CODE") {
        throw new Error(
            `Reuse reset code: expected INVALID_RESET_CODE, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 5: Listar usuários como admin após cadastros

    res = await http(
        "GET",
        "/user?page=0&size=50",
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

    let mainNickname = await getNickname(mainUser);
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

    if (!res.body.data.content.some(user => user.nickname === mainNickname)) {
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
        200,
        "Find unknown user by username"
    );

    if (res.body.data.content.length !== 0) {
        throw new Error(
            `Find unknown user by username: expected empty content, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 8: Cadastrar definição de item, conceder ao usuário e ver itens próprios

    const itemName = `integration-user-${stamp}`;

    res = await registerItem(
        "/admin/items",
        {
            name: itemName,
            kind: "EQUIPPABLE",
            category: "AVATAR",
            context: "PROFILE"
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register item definition"
    );

    const itemId = res.body?.data?.itemId;

    if (!itemId) {
        throw new Error(
            `Register item definition: missing id in response body=${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "PATCH",
        `/user/${mainUser.id}/grant-reward`,
        {
            rewardType: "ITEM",
            quantity: 1,
            rewardReference: itemId
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Grant item reward"
    );

    res = await http(
        "GET",
        "/user/items",
        undefined,
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Get my items"
    );

    if (!res.body.data.items.some(item => item.itemId === itemId)) {
        throw new Error(
            "Get my items: granted item not found"
        );
    }

    // Fluxo 9: Perfil público ainda sem equipados (visão por username)

    res = await http(
        "GET",
        `/user/username/${mainNickname}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get user profile"
    );

    if (res.body.data.content.some(user => (user.equipped ?? []).some(item => item.itemId === itemId))) {
        throw new Error(
            "Get user profile: item should not be equipped yet"
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

    // Fluxo 11: Troca de nickname válido, em uso e inválido (consome item de troca de nome)

    const nicknameItemName = `integration-nickname-${stamp}`;

    res = await registerItem(
        "/admin/items",
        {
            name: nicknameItemName,
            kind: "CONSUMABLE",
            effectKind: "NICKNAME_CHANGE"
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register nickname item definition"
    );

    const nicknameItemId = res.body?.data?.itemId;

    if (!nicknameItemId) {
        throw new Error(
            `Register nickname item definition: missing id in response body=${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "PATCH",
        `/user/${mainUser.id}/grant-reward`,
        {
            rewardType: "ITEM",
            quantity: 1,
            rewardReference: nicknameItemId
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Grant nickname item reward"
    );

    res = await http(
        "PATCH",
        "/user/nickname",
        {
            nickname: "ab",
            itemId: nicknameItemId
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
            nickname: secondNickname,
            itemId: nicknameItemId
        },
        mainUser.token
    );

    ensureStatus(
        res,
        409,
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
            nickname: renamedNickname,
            itemId: nicknameItemId
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

    mainNickname = renamedNickname;

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
            nickname: `another${stamp % 100000}`,
            itemId: nicknameItemId
        },
        mainUser.token
    );

    ensureStatus(
        res,
        404,
        "Change nickname without owning item"
    );

    if (res.body?.code !== "ITEM_NOT_OWNED") {
        throw new Error(
            `Change nickname without owning item: expected ITEM_NOT_OWNED, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 12: Equipar item possuído e não possuído

    res = await http(
        "POST",
        `/user/items/${itemId}/equip`,
        { context: "PROFILE" },
        mainUser.token
    );

    ensureStatus(
        res,
        200,
        "Equip owned item"
    );

    const equipped = res.body.data.movements.find(item => item.itemId === itemId);

    if (!equipped || equipped.equippedAfter !== true) {
        throw new Error(
            "Equip owned item: item was not equipped"
        );
    }

    res = await http(
        "GET",
        `/user/username/${mainNickname}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get user profile after equip"
    );

    if (!res.body.data.content.some(user => (user.equipped ?? []).some(item => item.itemId === itemId))) {
        throw new Error(
            "Get user profile after equip: equipped item not found"
        );
    }

    // Item existente mas não possuído: definição própria do mainUser nunca
    // concedida, então o equip falha com ITEM_NOT_OWNED (e não ITEM_NOT_FOUND).

    const unownedItemName = `integration-unowned-${stamp}`;

    res = await registerItem(
        "/admin/items",
        {
            name: unownedItemName,
            kind: "EQUIPPABLE",
            category: "FRAME",
            context: "PROFILE"
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register unowned item definition"
    );

    const unownedItemId = res.body?.data?.itemId;

    if (!unownedItemId) {
        throw new Error(
            `Register unowned item definition: missing id in response body=${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "POST",
        `/user/items/${unownedItemId}/equip`,
        { context: "PROFILE" },
        mainUser.token
    );

    ensureStatus(
        res,
        404,
        "Equip unowned item"
    );

    if (res.body?.code !== "ITEM_NOT_OWNED") {
        throw new Error(
            `Equip unowned item: expected ITEM_NOT_OWNED, received ${JSON.stringify(res.body)}`
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
        403,
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

    // Fluxo 17: Revogar item — permissão e posse no contrato atual.
    // DELETE /user/items/{id} exige USER:EDIT (RevokeItemUseCase) e opera
    // sobre o inventário do próprio chamador (sem userId na rota). Usuário
    // comum não passa no AdminChecker, e o caminho feliz via HTTP é
    // inalcançável (todos os writers exigem um User real; coberto pelo
    // RevokeItemUseCaseTest unitário). O fluxo preserva a garantia adaptada:
    // (a) usuário comum recebe 403 ao tentar revogar; (b) admin sem posse
    // recebe ITEM_NOT_OWNED (404); (c) o item concedido permanece no inventário.

    const revokeItemName = `integration-revoke-${stamp}`;

    res = await registerItem(
        "/admin/items",
        {
            name: revokeItemName,
            kind: "EQUIPPABLE",
            category: "BANNER",
            context: "PROFILE"
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register revoke item definition"
    );

    const revokeItemId = res.body?.data?.itemId;

    if (!revokeItemId) {
        throw new Error(
            `Register revoke item definition: missing id in response body=${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "PATCH",
        `/user/${secondUser.id}/grant-reward`,
        {
            rewardType: "ITEM",
            quantity: 1,
            rewardReference: revokeItemId
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Grant revoke item reward"
    );

    res = await http(
        "DELETE",
        `/user/items/${revokeItemId}`,
        undefined,
        secondUser.token
    );

    ensureStatus(
        res,
        403,
        "Revoke item without permission"
    );

    res = await http(
        "DELETE",
        `/user/items/${revokeItemId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        404,
        "Revoke unowned item"
    );

    if (res.body?.code !== "ITEM_NOT_OWNED") {
        throw new Error(
            `Revoke unowned item: expected ITEM_NOT_OWNED, received ${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "GET",
        "/user/items",
        undefined,
        secondUser.token
    );

    ensureStatus(
        res,
        200,
        "Get items after revoke attempts"
    );

    if (!res.body.data.items.some(item => item.itemId === revokeItemId)) {
        throw new Error(
            "Get items after revoke attempts: granted item missing"
        );
    }

    // Fluxo 18: Sessao persistente com Refresh Token e rotacao.
    // A -> B -> C, reutilizacao de A rejeitada, logout revoga, novo login substitui.

    res = await http(
        "POST",
        "/user/auth",
        {
            email: mainUser.email,
            password: mainUser.password
        }
    );

    ensureStatus(
        res,
        200,
        "Auth user for session flow"
    );

    const refreshA = res.body?.data?.refreshToken;

    if (!refreshA) {
        throw new Error(
            `Auth user for session flow: refreshToken not found body=${JSON.stringify(res.body)}`
        );
    }

    mainUser.setAuth(res.body.data);

    res = await http(
        "POST",
        "/user/auth/refresh",
        { refreshToken: refreshA }
    );

    ensureStatus(
        res,
        200,
        "Refresh session A -> B"
    );

    const refreshB = res.body?.data?.refreshToken;
    const accessB = res.body?.data?.token;

    if (!refreshB || !accessB || refreshB === refreshA) {
        throw new Error(
            `Refresh session A -> B: expected rotated pair body=${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "GET",
        "/user/me",
        undefined,
        accessB
    );

    ensureStatus(
        res,
        200,
        "Get profile with rotated access token"
    );

    res = await http(
        "POST",
        "/user/auth/refresh",
        { refreshToken: refreshB }
    );

    ensureStatus(
        res,
        200,
        "Refresh session B -> C"
    );

    const refreshC = res.body?.data?.refreshToken;

    if (!refreshC || refreshC === refreshB) {
        throw new Error(
            `Refresh session B -> C: expected rotated pair body=${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "POST",
        "/user/auth/refresh",
        { refreshToken: refreshA }
    );

    ensureStatus(
        res,
        401,
        "Reject ancient refresh token"
    );

    res = await http(
        "POST",
        "/user/auth/refresh",
        { refreshToken: refreshC }
    );

    ensureStatus(
        res,
        401,
        "Reject refresh after reuse revocation"
    );

    res = await http(
        "POST",
        "/user/auth",
        {
            email: mainUser.email,
            password: mainUser.password
        }
    );

    ensureStatus(
        res,
        200,
        "Auth user after session revocation"
    );

    const refreshD = res.body?.data?.refreshToken;
    const accessD = res.body?.data?.token;

    if (!refreshD) {
        throw new Error(
            "Auth user after session revocation: refreshToken not found"
        );
    }

    mainUser.setAuth(res.body.data);

    res = await http(
        "POST",
        "/user/auth/logout",
        undefined,
        accessD
    );

    ensureStatus(
        res,
        204,
        "Logout"
    );

    res = await http(
        "POST",
        "/user/auth/refresh",
        { refreshToken: refreshD }
    );

    ensureStatus(
        res,
        401,
        "Reject refresh after logout"
    );

    if (res.body?.code !== "SESSION_REVOKED") {
        throw new Error(
            `Reject refresh after logout: expected SESSION_REVOKED, received ${JSON.stringify(res.body)}`
        );
    }

    res = await http(
        "POST",
        "/user/auth",
        {
            email: mainUser.email,
            password: mainUser.password
        }
    );

    ensureStatus(
        res,
        200,
        "Auth user after logout"
    );

    if (!res.body?.data?.refreshToken) {
        throw new Error(
            "Auth user after logout: refreshToken not found"
        );
    }

    mainUser.setAuth(res.body.data);
}
