import { endpoint } from "./config.js";

export async function http(method, path, body, token) {

    const response = await fetch(`${endpoint}${path}`, {
        method,
        headers: {
            "Content-Type": "application/json",
            Authorization: token ? `Bearer ${token}` : undefined
        },
        body: body ? JSON.stringify(body) : undefined
    });

    const text = await response.text();

    return {
        status: response.status,
        body: text ? JSON.parse(text) : null
    }
}