import { http } from "../core/http.js";

function ensureStatus(response, expected, operation) {
    const expectedStatus = Array.isArray(expected) ? expected : [expected];

    if (!expectedStatus.includes(response.status)) {
        throw new Error(
            `${operation}: expected ${expectedStatus.join(" or ")}, received ${response.status} =-=-= ${JSON.stringify(response.body)}`
        );
    }
}

export async function runFlow(context) {
    const [admin] = context.admins;

    let res;

    // Fluxo 1: Criar level

    res = await http(
        "POST",
        "/level",
        {
            level: 99,
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
            ]
        },
        admin.token
    );

    ensureStatus(res, 200, "Create level");

    const level = res.body.data.level;

    const levelId = level.levelId;

    if (level.value !== 99) {
        throw new Error("Create level: invalid level");
    }

    if (level.rewards.length !== 2) {
        throw new Error("Create level: invalid rewards");
    }

    // Fluxo 2: Buscar todos os levels

    res = await http(
        "GET",
        "/level",
        undefined,
        admin.token
    );

    ensureStatus(res, 200, "Get levels");

    if (!res.body.data.content.some(l => l.levelId === levelId)) {
        throw new Error("Created level not found");
    }

    // Fluxo 3: Atualizar level

    res = await http(
        "PUT",
        `/level/${levelId}`,
        {
            level: 100,
            rewards: [
                {
                    rewardType: "COIN",
                    rewardReference: null,
                    quantity: 1000
                }
            ]
        },
        admin.token
    );

    ensureStatus(res, 200, "Update level");

    // Fluxo 4: Buscar novamente

    res = await http(
        "GET",
        "/level",
        undefined,
        admin.token
    );

    ensureStatus(res, 200, "Get updated levels");

    const updated = res.body.data.content.find(l => l.levelId === levelId);

    if (!updated) {
        throw new Error("Updated level not found");
    }

    if (updated.value !== 100) {
        throw new Error("Level was not updated");
    }

    if (updated.rewards.length !== 1) {
        throw new Error("Rewards were not updated");
    }

    // Fluxo 5: Buscar level por id

    res = await http(
        "GET",
        `/level/${levelId}`,
        undefined,
        admin.token
    );

    ensureStatus(res, 200, "Get level by id");

    if (res.body.data.level.levelId !== levelId) {
        throw new Error("Get level by id: id mismatch");
    }

    if (res.body.data.level.value !== 100) {
        throw new Error("Get level by id: value mismatch");
    }

    // Fluxo 6: Buscar level por valor

    res = await http(
        "GET",
        "/level/value/100",
        undefined,
        admin.token
    );

    ensureStatus(res, 200, "Get level by value");

    if (res.body.data.level.levelId !== levelId) {
        throw new Error("Get level by value: id mismatch");
    }

    if (res.body.data.level.value !== 100) {
        throw new Error("Get level by value: value mismatch");
    }

    res = await http(
        "GET",
        "/level/value/999999",
        undefined,
        admin.token
    );

    ensureStatus(res, 400, "Get unknown level by value");

    if (res.body?.code !== "LEVEL_NOT_FOUND") {
        throw new Error(
            `Get unknown level by value: expected LEVEL_NOT_FOUND, received ${JSON.stringify(res.body)}`
        );
    }

    // Fluxo 7: Remover level

    /* res = await http(
        "DELETE",
        `/level/${levelId}`,
        undefined,
        admin.token
    );

    ensureStatus(res, 200, "Delete level");

    // Fluxo 8: Confirmar remoção

    res = await http(
        "GET",
        "/levels",
        undefined,
        admin.token
    );

    ensureStatus(res, 200, "Get levels after delete");

    if (res.data.data.some(l => l.levelId === levelId)) {
        throw new Error("Deleted level still exists");
    } */
}