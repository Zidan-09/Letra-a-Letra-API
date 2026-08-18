import WebSocket from "ws";
import { websocket } from "./config.js";

export function connect(user, events, onEvent) {
    return new Promise((resolve, reject) => {
        const ws = new WebSocket(`${websocket}${user.token}`);

        ws.on("open", () => resolve(ws));
        ws.on("error", (err) => reject(err));

        ws.on("message", data => {
            const message = JSON.parse(data);

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