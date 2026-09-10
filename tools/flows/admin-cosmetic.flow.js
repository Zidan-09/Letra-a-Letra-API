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

export async function runFlow(context) {
    const [admin] = context.admins;

    const stamp = Date.now();
    const baseName = `integration-cosmetic-${stamp}`;
    const updatedName = `${baseName}-updated`;

    let res;

    // Fluxo 1: Cadastrar cosmético (multipart: name, cosmeticType, asset)

    const asset = imageAsset("avatar.png");

    const form = new FormData();

    form.append("name", baseName);
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

    // Fluxo 2: Listar cosméticos e conferir o cadastrado

    res = await http(
        "GET",
        "/cosmetic?page=0&size=10",
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Get cosmetics"
    );

    if (!res.body.data.content.some(c => c.id === cosmeticId)) {
        throw new Error(
            "Get cosmetics: created cosmetic not found"
        );
    }

    // Fluxo 3: Buscar por nome parcial

    res = await http(
        "GET",
        `/cosmetic/search?search=integration-cosmetic&page=0&size=10`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Search cosmetic"
    );

    if (!res.body.data.content.some(c => c.id === cosmeticId)) {
        throw new Error(
            "Search cosmetic: created cosmetic not found"
        );
    }

    // Fluxo 4: Buscar por nome exato e por nome inexistente

    res = await http(
        "GET",
        `/cosmetic/name/${baseName}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Find cosmetic by name"
    );

    if (res.body.data.cosmetic.id !== cosmeticId) {
        throw new Error(
            "Find cosmetic by name: id mismatch"
        );
    }

    res = await http(
        "GET",
        `/cosmetic/name/unknown-cosmetic-${stamp}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        400,
        "Find unknown cosmetic by name"
    );

    // Fluxo 5: Atualizar cosmético (PUT é @ModelAttribute: multipart
    // com name, type e isNewAsset; sem novo asset mantém o atual)

    const updateForm = new FormData();

    updateForm.append("name", updatedName);
    updateForm.append("type", "AVATAR");
    updateForm.append("isNewAsset", "false");

    res = await multipart(
        "PUT",
        `/cosmetic/${cosmeticId}`,
        updateForm,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Update cosmetic"
    );

    if (res.body.data.cosmetic.name !== updatedName) {
        throw new Error(
            "Update cosmetic: name was not updated"
        );
    }

    res = await http(
        "GET",
        `/cosmetic/name/${updatedName}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Find updated cosmetic by name"
    );

    if (res.body.data.cosmetic.id !== cosmeticId) {
        throw new Error(
            "Find updated cosmetic by name: id mismatch"
        );
    }

    res = await http(
        "GET",
        `/cosmetic/name/${baseName}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        400,
        "Find old cosmetic name after update"
    );

    // Fluxo 6: Desativar e conferir reflexo

    res = await http(
        "PATCH",
        `/cosmetic/disable/${cosmeticId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Disable cosmetic"
    );

    if (res.body.data.cosmetic.available !== false) {
        throw new Error(
            "Disable cosmetic: available flag was not set to false"
        );
    }

    res = await http(
        "GET",
        `/cosmetic/name/${updatedName}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Find disabled cosmetic by name"
    );

    if (res.body.data.cosmetic.available !== false) {
        throw new Error(
            "Disable cosmetic: list lookup still shows available"
        );
    }

    // Fluxo 7: Reativar após desativação

    res = await http(
        "PATCH",
        `/cosmetic/enable/${cosmeticId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Enable cosmetic"
    );

    if (res.body.data.cosmetic.available !== true) {
        throw new Error(
            "Enable cosmetic: available flag was not set to true"
        );
    }

    // Fluxo 8: Remover e conferir que não é mais encontrado

    res = await http(
        "DELETE",
        `/cosmetic/${cosmeticId}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Delete cosmetic"
    );

    res = await http(
        "GET",
        `/cosmetic/name/${updatedName}`,
        undefined,
        admin.token
    );

    ensureStatus(
        res,
        400,
        "Find deleted cosmetic by name"
    );

    if (res.body?.code !== "COSMETIC_NOT_FOUND") {
        throw new Error(
            `Find deleted cosmetic by name: expected COSMETIC_NOT_FOUND, received ${JSON.stringify(res.body)}`
        );
    }
}
