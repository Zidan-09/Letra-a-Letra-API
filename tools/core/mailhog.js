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

const UUID_PATTERN = "([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})";

export function extractTokenByPath(message, path) {
    const parts = [];

    collectText(message ?? {}, parts);

    const raw = decodeQuotedPrintableFragment(parts.join("\n"));
    const match = raw.match(new RegExp(path + "\\?token=" + UUID_PATTERN));

    return match ? match[1] : undefined;
}

export function extractToken(message) {
    return extractTokenByPath(message, "ativar-conta");
}

export function extractResetToken(message) {
    return extractTokenByPath(message, "redefinir-senha");
}

function findToken(messages, recipientEmail, extractor) {
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

        const token = extractor(message);

        if (token) {
            return token;
        }
    }

    return undefined;
}

async function waitForMailToken(recipientEmail, extractor, label, { timeoutMs = 30000, intervalMs = 500 } = {}) {
    const deadline = Date.now() + timeoutMs;
    let lastError;

    while (Date.now() < deadline) {
        try {
            const messages = await fetchMessages();
            const token = findToken(messages, recipientEmail, extractor);

            if (token) {
                return token;
            }
        } catch (error) {
            lastError = error;
        }

        await sleep(intervalMs);
    }

    throw new Error(
        `MailHog: ${label} email for ${recipientEmail} not received` +
        (lastError ? ` (last error: ${lastError.message})` : "")
    );
}

export async function waitForAdminActivationToken(
    recipientEmail,
    options
) {
    return waitForMailToken(recipientEmail, extractToken, "activation", options);
}

export async function waitForAdminResetToken(
    recipientEmail,
    options
) {
    return waitForMailToken(recipientEmail, extractResetToken, "password reset", options);
}
