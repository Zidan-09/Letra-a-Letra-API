import WebSocket from "ws";
import { websocket } from "./config.js";

export function connect(user, events) {

    return new Promise(resolve => {

        const ws = new WebSocket(`${websocket}?token=${user.token}`);

        ws.on("open", () => resolve(ws));

        ws.on("message", data => {
            const message = JSON.parse(data);

            console.log(user.nickname, message);

            events.push({
                ...message,
                user: user.nickname
            });
        });
    });
}

export function send(ws, payload) {
    ws.send(JSON.stringify(payload));
}