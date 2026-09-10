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

async function getCoins(user) {
    const res = await http(
        "GET",
        "/user/me",
        undefined,
        user.token
    );

    ensureStatus(
        res,
        200,
        "Get balance"
    );

    return res.body.data.user.wallet.coins;
}

export async function runFlow(adminContext, playerContext) {
    const [admin] = adminContext.admins;
    const [buyer, poorUser] = playerContext.users;

    const stamp = Date.now();

    let res;

    // Fluxo 1: Cadastrar cosmético de recompensa e oferta como admin

    const asset = imageAsset("avatar.png");
    const cosmeticName = `integration-shop-${stamp}`;

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

    const title = `integration-shop-offer-${stamp}`;

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
                    quantity: 50
                },
                {
                    rewardType: "COSMETIC",
                    rewardReference: cosmeticId,
                    quantity: 1
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

    // Fluxo 2: Habilitar oferta e listar como jogador

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

    res = await http(
        "GET",
        "/shop/offers",
        undefined,
        buyer.token
    );

    ensureStatus(
        res,
        200,
        "Get active offers"
    );

    if (!res.body.data.offers.some(offer => offer.offerId === offerId)) {
        throw new Error(
            "Get active offers: created offer not found"
        );
    }

    // Fluxo 3: Comprar sem saldo suficiente

    res = await http(
        "POST",
        `/shop/offers/${offerId}/buy`,
        undefined,
        poorUser.token
    );

    ensureStatus(
        res,
        400,
        "Buy offer with insufficient balance"
    );

    if (res.body?.code !== "INSUFFICIENT_BALANCE") {
        throw new Error(
            `Buy offer with insufficient balance: expected INSUFFICIENT_BALANCE, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 4: Comprar com sucesso (débito + crédito)

    res = await http(
        "PATCH",
        `/user/${buyer.id}/grant-reward`,
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
        "Grant coins to buyer"
    );

    const coinsBefore = await getCoins(buyer);

    res = await http(
        "POST",
        `/shop/offers/${offerId}/buy`,
        undefined,
        buyer.token
    );

    ensureStatus(
        res,
        200,
        "Buy offer"
    );

    if (res.body.data.offer.offerId !== offerId) {
        throw new Error(
            "Buy offer: id mismatch"
        );
    }

    const coinsAfter = await getCoins(buyer);

    if (coinsAfter - coinsBefore !== -50) {
        throw new Error(
            `Buy offer: expected net -50 coins (100 debit + 50 credit), received ${coinsAfter - coinsBefore}`
        );
    }

    res = await http(
        "GET",
        "/user/inventory",
        undefined,
        buyer.token
    );

    ensureStatus(
        res,
        200,
        "Get inventory after purchase"
    );

    if (!res.body.data.inventory.some(item => item.cosmeticId === cosmeticId)) {
        throw new Error(
            "Get inventory after purchase: purchased cosmetic not found"
        );
    }

    res = await http(
        "GET",
        "/user/transactions?page=0&size=20",
        undefined,
        buyer.token
    );

    ensureStatus(
        res,
        200,
        "Get transactions after purchase"
    );

    if (!res.body.data.content.some(item => item.reason === "SHOP_PURCHASE" && item.operation === "DEBIT" && item.amount === 100)) {
        throw new Error(
            "Get transactions after purchase: purchase debit not found"
        );
    }

    // Fluxo 5: Recomprar item único

    res = await http(
        "POST",
        `/shop/offers/${offerId}/buy`,
        undefined,
        buyer.token
    );

    ensureStatus(
        res,
        400,
        "Rebuy single offer"
    );

    if (res.body?.code !== "OFFER_ALREADY_PURCHASED") {
        throw new Error(
            `Rebuy single offer: expected OFFER_ALREADY_PURCHASED, received ${JSON.stringify(res.body)}`
        );
    }
}
