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

export async function runFlow(context) {
    const [admin] = context.admins;

    const stamp = Date.now();
    const title = `integration-offer-${stamp}`;

    let res;

    // Fluxo 1: Cadastrar oferta com recompensas

    res = await http(
        "POST",
        "/offer",
        {
            title,
            coinType: "SOFT",
            price: 100,
            rewards: [
                {
                    rewardType: "COIN",
                    rewardReference: null,
                    quantity: 500
                },
                {
                    rewardType: "GEMS",
                    rewardReference: null,
                    quantity: 25
                }
            ],
            repeatable: false,
            hasExpiration: false,
            expiresIn: 0
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Register offer"
    );

    const offerId = res.body?.data?.offer?.offerId;

    if (!offerId) {
        throw new Error(
            `Register offer: missing id in response body=${JSON.stringify(res.body)}`
        );
    }

    if (res.body.data.offer.title !== title) {
        throw new Error(
            "Register offer: title mismatch"
        );
    }

    if (res.body.data.offer.rewards.length !== 2) {
        throw new Error(
            "Register offer: invalid rewards"
        );
    }

    // Fluxo 2: Listar ofertas e conferir a cadastrada

    res = await http(
        "GET",
        "/offer?page=0&size=10",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get offers"
    );

    if (!res.body.data.content.some(o => o.offerId === offerId)) {
        throw new Error(
            "Get offers: created offer not found"
        );
    }

    // Fluxo 3: Buscar oferta por id existente e inexistente

    res = await http(
        "GET",
        `/offer/${offerId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Find offer by id"
    );

    if (res.body.data.offer.offerId !== offerId) {
        throw new Error(
            "Find offer by id: id mismatch"
        );
    }

    res = await http(
        "GET",
        "/offer/00000000-0000-0000-0000-000000000000",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        400,
        "Find unknown offer by id"
    );

    if (res.body?.code !== "OFFER_NOT_FOUND") {
        throw new Error(
            `Find unknown offer by id: expected OFFER_NOT_FOUND, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 4: Ativar oferta (cadastrada como inativa)

    res = await http(
        "PATCH",
        `/offer/enable/${offerId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Enable offer"
    );

    if (res.body.data.offer.active !== true) {
        throw new Error(
            "Enable offer: active flag was not set to true"
        );
    }

    // Fluxo 5: Desativar oferta

    res = await http(
        "PATCH",
        `/offer/disable/${offerId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Disable offer"
    );

    if (res.body.data.offer.active !== false) {
        throw new Error(
            "Disable offer: active flag was not set to false"
        );
    }

    // Fluxo 6: Reativar após desativação

    res = await http(
        "PATCH",
        `/offer/enable/${offerId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Re-enable offer"
    );

    if (res.body.data.offer.active !== true) {
        throw new Error(
            "Re-enable offer: active flag was not set to true"
        );
    }

    // Fluxo 7: Remover e conferir que não é mais encontrada

    res = await http(
        "DELETE",
        `/offer/${offerId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Delete offer"
    );

    res = await http(
        "GET",
        `/offer/${offerId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        400,
        "Find deleted offer by id"
    );

    if (res.body?.code !== "OFFER_NOT_FOUND") {
        throw new Error(
            `Find deleted offer by id: expected OFFER_NOT_FOUND, received ${JSON.stringify(res.body)}`
        );
    }
}
