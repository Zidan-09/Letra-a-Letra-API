import { multipart } from "./multipart.js";

const PNG_1X1 = Buffer.from(
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==",
    "base64"
);

export async function registerItem(path, item, token) {
    const form = new FormData();
    for (const key of ["name", "kind", "category", "context", "effectKind", "effectType", "magnitude", "durationMinutes"]) {
        if (item[key] !== undefined && item[key] !== null) {
            form.append(key, String(item[key]));
        }
    }
    if (item.kind !== "CONSUMABLE") {
        form.append("asset", new Blob([PNG_1X1], { type: "image/png" }), `${item.name}.png`);
    }

    return multipart("POST", path, form, token);
}
