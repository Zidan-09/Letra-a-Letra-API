import { http } from "../core/http.js";

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

function assertAllMatch(content, fields, operation) {
    for (const item of content) {
        for (const [field, expected] of Object.entries(fields)) {
            if (item[field] !== expected) {
                throw new Error(
                    `${operation}: expected ${field}=${expected}, received ${JSON.stringify(item)}`
                );
            }
        }
    }
}

export async function runFlow(adminContext, playerContext) {
    const [admin] = adminContext.admins;
    const [user] = playerContext.users;

    const stamp = Date.now();

    let res;

    // Fluxo 1: Conceder recompensa de moedas ao usuário (gera WALLET_CREDITED)

    res = await http(
        "PATCH",
        `/user/${user.id}/grant-reward`,
        {
            rewardType: "COIN",
            quantity: 100,
            rewardReference: null
        },
        admin.token
    );

    ensureStatus(
        res,
        204,
        "Grant coin reward"
    );

    // Fluxo 2: Cadastrar definição de item e concedê-la ao usuário (gera ITEM_ACQUIRED)

    const itemName = `integration-audit-${stamp}`;

    res = await http(
        "POST",
        "/admin/items",
        {
            name: itemName,
            kind: "COSMETIC",
            category: "AVATAR",
            applicability: ["PROFILE"],
            stackable: false,
            consumable: false,
            assetPath: `assets/${itemName}.png`
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
        `/user/${user.id}/grant-reward`,
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

    // Fluxo 3: Listar eventos de auditoria sem filtros

    res = await http(
        "GET",
        "/admin/audit?page=0&size=20",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get audit events"
    );

    const walletEvent = res.body.data.content.find(
        item => item.eventType === "WALLET_CREDITED" && item.targetUserId === user.id
    );

    if (!walletEvent) {
        throw new Error(
            "Get audit events: WALLET_CREDITED event for user not found"
        );
    }

    assertAllMatch(
        [walletEvent],
        {
            category: "ECONOMY",
            outcome: "SUCCESS",
            resourceType: "WALLET",
            resourceId: user.id,
            actorId: admin.id
        },
        "Get audit events"
    );

    // Fluxo 4: Listar eventos com filtros e paginação

    res = await http(
        "GET",
        `/admin/audit?eventType=WALLET_CREDITED&targetUserId=${user.id}&page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get audit events filtered"
    );

    if (res.body.data.content.length === 0) {
        throw new Error(
            "Get audit events filtered: expected at least one event"
        );
    }

    assertAllMatch(
        res.body.data.content,
        {
            eventType: "WALLET_CREDITED",
            targetUserId: user.id
        },
        "Get audit events filtered"
    );

    res = await http(
        "GET",
        `/admin/audit?category=ECONOMY&outcome=SUCCESS&actorId=${admin.id}&resourceType=WALLET&resourceId=${user.id}&page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get audit events filtered by category"
    );

    if (res.body.data.content.length === 0) {
        throw new Error(
            "Get audit events filtered by category: expected at least one event"
        );
    }

    assertAllMatch(
        res.body.data.content,
        {
            category: "ECONOMY",
            outcome: "SUCCESS",
            actorId: admin.id,
            resourceType: "WALLET",
            resourceId: user.id
        },
        "Get audit events filtered by category"
    );

    const from = new Date(Date.now() - 3600 * 1000).toISOString();
    const to = new Date(Date.now() + 3600 * 1000).toISOString();

    res = await http(
        "GET",
        `/admin/audit?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&direction=ASC&page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get audit events filtered by period"
    );

    if (!res.body.data.content.some(item => item.eventType === "WALLET_CREDITED" && item.targetUserId === user.id)) {
        throw new Error(
            "Get audit events filtered by period: WALLET_CREDITED event for user not found"
        );
    }

    res = await http(
        "GET",
        "/admin/audit?page=0&size=1",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get audit events paginated"
    );

    if (res.body.data.content.length > 1) {
        throw new Error(
            "Get audit events paginated: page size not respected"
        );
    }

    for (const field of ["page", "size", "totalElements", "totalPages", "first", "last"]) {
        if (res.body.data[field] === undefined) {
            throw new Error(
                `Get audit events paginated: missing page field ${field}`
            );
        }
    }

    res = await http(
        "GET",
        "/admin/audit?transactionId=00000000-0000-0000-0000-000000000000&page=0&size=20",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get audit events with unknown transaction"
    );

    if (res.body.data.content.length !== 0) {
        throw new Error(
            "Get audit events with unknown transaction: expected empty content"
        );
    }

    // Fluxo 5: Histórico de auditoria do usuário

    res = await http(
        "GET",
        `/admin/audit/user/${user.id}?page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get user audit history"
    );

    if (!res.body.data.content.some(item => item.eventType === "WALLET_CREDITED" && item.targetUserId === user.id)) {
        throw new Error(
            "Get user audit history: WALLET_CREDITED event not found"
        );
    }

    res = await http(
        "GET",
        `/admin/audit/user/${user.id}?eventType=ITEM_ACQUIRED&page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get user audit history filtered"
    );

    if (!res.body.data.content.some(item => item.eventType === "ITEM_ACQUIRED" && item.targetUserId === user.id)) {
        throw new Error(
            "Get user audit history filtered: ITEM_ACQUIRED event not found"
        );
    }

    assertAllMatch(
        res.body.data.content,
        { eventType: "ITEM_ACQUIRED" },
        "Get user audit history filtered"
    );

    res = await http(
        "GET",
        "/admin/audit/user/00000000-0000-0000-0000-000000000000?page=0&size=20",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get unknown user audit history"
    );

    if (res.body.data.content.length !== 0) {
        throw new Error(
            "Get unknown user audit history: expected empty content"
        );
    }

    // Fluxo 6: Histórico de auditoria por recurso

    res = await http(
        "GET",
        `/admin/audit/resource/WALLET/${user.id}?page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get wallet audit history"
    );

    if (!res.body.data.content.some(item => item.eventType === "WALLET_CREDITED" && item.resourceId === user.id)) {
        throw new Error(
            "Get wallet audit history: WALLET_CREDITED event not found"
        );
    }

    assertAllMatch(
        res.body.data.content,
        {
            resourceType: "WALLET",
            resourceId: user.id
        },
        "Get wallet audit history"
    );

    res = await http(
        "GET",
        `/admin/audit/resource/INVENTORY_ITEM/${itemId}?page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get item audit history"
    );

    if (!res.body.data.content.some(item => item.eventType === "ITEM_ACQUIRED" && item.resourceId === itemId)) {
        throw new Error(
            "Get item audit history: ITEM_ACQUIRED event not found"
        );
    }

    assertAllMatch(
        res.body.data.content,
        {
            resourceType: "INVENTORY_ITEM",
            resourceId: itemId
        },
        "Get item audit history"
    );
}
