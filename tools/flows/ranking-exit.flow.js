import { waitForEvent } from "../core/waitForEvent.js";
import { send } from "../core/websocket.js";
import { sleep } from "../core/sleep.js";

export async function runFlow(context) {
    const [ws] = context.sockets;
    const events = context.getSharedEvents();

    // Fluxo 1: Entrar sozinho na fila ranqueada

    send(ws, {
        type: "RANKING_GAME"
    });

    await sleep(3000);

    if (events.some(e => e.event === "RANKING_GAME" && e.status === "FOUNDED")) {
        throw new Error(
            "EXIT_RANKING: unexpected pairing with a single player in queue"
        );
    }

    // Fluxo 2: Sair da fila antes do pareamento

    send(ws, {
        type: "EXIT_RANKING"
    });

    const left = await waitForEvent("EXIT_RANKING", e => e.event === "EXIT_RANKING", events);

    if (left.message !== "USER_LEFT_QUEUE") {
        throw new Error(
            `EXIT_RANKING: unexpected message ${JSON.stringify(left)}`
        );
    }

    // Fluxo 3: Garantir que não houve pareamento após a saída

    const mark = events.length;

    await sleep(10000);

    const paired = events.slice(mark).some(e => e.event === "RANKING_GAME" && e.status === "FOUNDED");

    if (paired) {
        throw new Error(
            "EXIT_RANKING: player was paired after leaving the queue"
        );
    }
}
