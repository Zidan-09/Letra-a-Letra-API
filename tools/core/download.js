import { endpoint } from "./config.js";

export async function download(
    method,
    path,
    token
) {
    const response = await fetch(`${endpoint}${path}`, {
        method,
        headers: token ?
            {
                Authorization: `Bearer ${token}`
            }
            : undefined
        }
    );

    return {
        status: response.status,
        data: await response.text()
    };
}