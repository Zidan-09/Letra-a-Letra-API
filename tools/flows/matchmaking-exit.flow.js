import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";

export async function runFlow(context) {
    const [ws] = context.sockets;
    const events = context.getSharedEvents();

    // Fluxo 1: Entrar sozinho na fila casual

    send(ws, {
        type: "MATCHMAKING_GAME",
        gameMode: "NORMAL"
    });

    await sleep(3000);

    if (events.some(e => e.event === "MATCHMAKING_GAME" && e.status === "FOUNDED")) {
        throw new Error(
            "EXIT_MATCHMAKING: unexpected pairing with a single player in queue"
        );
    }

    // Fluxo 2: Sair da fila antes do pareamento

    send(ws, {
        type: "EXIT_MATCHMAKING"
    });

    const left = await waitForEvent("EXIT_MATCHMAKING", e => e.event === "EXIT_MATCHMAKING", events);

    if (left.message !== "USER_LEFT_QUEUE") {
        throw new Error(
            `EXIT_MATCHMAKING: unexpected message ${JSON.stringify(left)}`
        );
    }

    // Fluxo 3: Garantir que não houve pareamento após a saída

    const mark = events.length;

    await sleep(10000);

    const paired = events.slice(mark).some(e => e.event === "MATCHMAKING_GAME" && e.status === "FOUNDED");

    if (paired) {
        throw new Error(
            "EXIT_MATCHMAKING: player was paired after leaving the queue"
        );
    }
}
