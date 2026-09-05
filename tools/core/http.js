import { endpoint } from "./config.js";

export async function http(method, path, body, token) {
    const headers = {
        "Content-Type": "application/json"
    };

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(`${endpoint}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined
    });

    const text = await response.text();

    let parsed = null;

    if (text) {
        try {
            parsed = JSON.parse(text);
        } catch {
            parsed = text;
        }
    }

    return {
        status: response.status,
        body: parsed
    };
}