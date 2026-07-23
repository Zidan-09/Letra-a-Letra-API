import WebSocket from "ws";
import { websocket } from "./config.js";

export function connect(user, events) {

    return new Promise(resolve => {

        const ws = new WebSocket(`${websocket}?token=${user.token}`);

        ws.on("open", () => resolve(ws));

        ws.on("message", data => {
            const message = JSON.parse(data);

            const duplicated = events.some(event =>
                event.event === message.event &&
                JSON.stringify(event.data) === JSON.stringify(message.data)
            );

            if (!duplicated) {
                events.push({
                    ...message,
                    user: user.nickname
                });

                console.log(message);
            }
        });
    });
}

export function send(ws, payload) {
    ws.send(JSON.stringify(payload));
}