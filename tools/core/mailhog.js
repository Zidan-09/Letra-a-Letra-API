import { sleep } from "./sleep.js";

const MAILHOG_BASE =
    process.env.MAILHOG_URL ?? "http://localhost:8025";

async function fetchMessages() {
    const response = await fetch(`${MAILHOG_BASE}/api/v2/messages`);

    if (!response.ok) {
        throw new Error(
            `MailHog: unexpected status ${response.status}`
        );
    }

    const payload = await response.json();

    return payload?.items ?? [];
}

function decodeQuotedPrintableFragment(text) {
    return text
        .replace(/=\r?\n/g, "")
        .replace(/=3D/gi, "=");
}

function collectText(value, out) {
    if (typeof value === "string") {
        out.push(value);
        return;
    }

    if (Array.isArray(value)) {
        value.forEach(item => collectText(item, out));
        return;
    }

    if (value && typeof value === "object") {
        Object.values(value).forEach(item => collectText(item, out));
    }
}

export function extractToken(message) {
    const parts = [];

    collectText(message ?? {}, parts);

    const raw = decodeQuotedPrintableFragment(parts.join("\n"));
    const match = raw.match(/ativar-conta\?token=([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})/);

    return match ? match[1] : undefined;
}

function findToken(messages, recipientEmail) {
    const wanted = recipientEmail.toLowerCase();

    const sorted = [...messages].sort(
        (a, b) => new Date(b?.Created ?? 0) - new Date(a?.Created ?? 0)
    );

    for (const message of sorted) {
        const headers = message?.Content?.Headers ?? {};
        const to = JSON.stringify(headers.To ?? headers.to ?? "");

        if (!to.toLowerCase().includes(wanted)) {
            continue;
        }

        const token = extractToken(message);

        if (token) {
            return token;
        }
    }

    return undefined;
}

export async function waitForAdminActivationToken(
    recipientEmail,
    { timeoutMs = 30000, intervalMs = 500 } = {}
) {
    const deadline = Date.now() + timeoutMs;
    let lastError;

    while (Date.now() < deadline) {
        try {
            const messages = await fetchMessages();
            const token = findToken(messages, recipientEmail);

            if (token) {
                return token;
            }
        } catch (error) {
            lastError = error;
        }

        await sleep(intervalMs);
    }

    throw new Error(
        `MailHog: activation email for ${recipientEmail} not received` +
        (lastError ? ` (last error: ${lastError.message})` : "")
    );
}
