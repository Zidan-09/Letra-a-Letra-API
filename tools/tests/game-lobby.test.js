import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/game-lobby.flow.js";

export async function run() {
    console.log("\n--------Init Game Lobby Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("LobbyPlayer");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);

    } finally {
        context.dispose();
    }
}
