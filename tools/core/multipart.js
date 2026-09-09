import { endpoint } from "./config.js";

export async function multipart(
    method,
    path,
    formData,
    token
) {
    const response = await fetch(
        `${endpoint}${path}`,
        {
            method,
            body: formData,
            headers: token
                ? {
                    Authorization: `Bearer ${token}`
                }
                : undefined
        }
    );

    const data = await response.json();

    return {
        status: response.status,
        data,
        body: data
    };
}