import WebSocket from "ws";
import { websocket } from "./config.js";

export function connect(user, events, onEvent, timeoutMs = 10000) {
    return new Promise((resolve, reject) => {
        if (!user?.token) {
            return reject(new Error(`connect: missing token for user ${user?.nickname ?? user?.email ?? "unknown"}`));
        }

        let settled = false;
        let timer;

        const cleanup = () => {
            if (timer) clearTimeout(timer);
        };

        const ws = new WebSocket(`${websocket}${user.token}`);

        timer = setTimeout(() => {
            if (settled) return;
            settled = true;
            try { ws.terminate?.(); } catch {}
            try { ws.close(); } catch {}
            reject(new Error(`WebSocket connect timeout (${timeoutMs}ms) for ${user.nickname} token=${String(user.token).slice(0,8)}...`));
        }, timeoutMs);

        ws.on("open", () => {
            if (settled) return;
            settled = true;
            cleanup();
            resolve(ws);
        });

        ws.on("error", (err) => {
            if (settled) return;
            settled = true;
            cleanup();
            reject(err);
        });

        ws.on("close", (code, reason) => {
            if (settled) return;
            settled = true;
            cleanup();
            reject(new Error(`WebSocket closed before open code=${code} reason=${String(reason)} user=${user.nickname}`));
        });

        ws.on("unexpected-response", (req, res) => {
            if (settled) return;
            settled = true;
            cleanup();
            let body = "";
            res.on("data", chunk => body += chunk);
            res.on("end", () => {
                reject(new Error(`WebSocket handshake failed status=${res.statusCode} body=${body.slice(0,500)} user=${user.nickname}`));
            });
        });

        ws.on("message", data => {
            let message;
            try {
                message = JSON.parse(data);
            } catch (e) {
                return;
            }

            const eventObj = {
                ...message,
                user: user.nickname
            };

            events.push(eventObj);
            onEvent?.(eventObj);

            if (events._listeners) {
                events._listeners = events._listeners.filter(
                    listener => !listener(eventObj)
                );
            }
        });
    });
}

export function send(ws, payload) {
    ws.send(JSON.stringify(payload));
}