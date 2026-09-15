import { multipart } from "./multipart.js";

const PNG_1X1 = Buffer.from(
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==",
    "base64"
);

export async function registerItem(path, item, token) {
    const form = new FormData();
    form.append("item", new Blob([JSON.stringify(item)], { type: "application/json" }));
    form.append("asset", new Blob([PNG_1X1], { type: "image/png" }), `${item.name}.png`);

    return multipart("POST", path, form, token);
}
