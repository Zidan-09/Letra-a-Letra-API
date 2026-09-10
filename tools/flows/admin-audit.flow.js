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

    // Fluxo 2: Cadastrar cosmético e concedê-lo ao usuário (gera COSMETIC_ACQUIRED)

    const asset = imageAsset("avatar.png");
    const cosmeticName = `integration-audit-${stamp}`;

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
        `/user/${user.id}/grant-reward`,
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
        `/admin/audit/user/${user.id}?eventType=COSMETIC_ACQUIRED&page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get user audit history filtered"
    );

    if (!res.body.data.content.some(item => item.eventType === "COSMETIC_ACQUIRED" && item.targetUserId === user.id)) {
        throw new Error(
            "Get user audit history filtered: COSMETIC_ACQUIRED event not found"
        );
    }

    assertAllMatch(
        res.body.data.content,
        { eventType: "COSMETIC_ACQUIRED" },
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
        `/admin/audit/resource/INVENTORY_ITEM/${cosmeticId}?page=0&size=20`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get cosmetic audit history"
    );

    if (!res.body.data.content.some(item => item.eventType === "COSMETIC_ACQUIRED" && item.resourceId === cosmeticId)) {
        throw new Error(
            "Get cosmetic audit history: COSMETIC_ACQUIRED event not found"
        );
    }

    assertAllMatch(
        res.body.data.content,
        {
            resourceType: "INVENTORY_ITEM",
            resourceId: cosmeticId
        },
        "Get cosmetic audit history"
    );
}
