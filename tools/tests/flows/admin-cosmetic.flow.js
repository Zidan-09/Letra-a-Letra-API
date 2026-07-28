import { http } from "../core/http.js";
import { multipart } from "../core/multipart.js";
import { sleep } from "../core/sleep.js";

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


    const form = new FormData();

    form.append(
        "name",
        "Integration Cosmetic"
    );

    form.append(
        "description",
        "Cosmetic created by integration test"
    );

    form.append(
        "cosmeticType",
        "AVATAR"
    );

    form.append(
        "rarity",
        "COMMON"
    );


    form.append(
        "asset",
        new Blob(
            ["fake-image-content"],
            {
                type: "image/png"
            }
        ),
        "avatar.png"
    );


    res = await multipart(
        "POST",
        "/cosmetic",
        form,
        admin.token
    );

    console.log(res);


    ensureStatus(
        res,
        200,
        "Register cosmetic"
    );


    const cosmeticId = res.data.data.id;


    res = await http(
        "PUT",
        `/cosmetic/${cosmeticId}`,
        {
            name: "Updated Cosmetic"
        },
        admin.token
    );

    ensureStatus(
        res,
        200,
        "Update cosmetic"
    );


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
}