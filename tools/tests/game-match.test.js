import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/game-match.flow.js";

export async function run() {
    console.log("\n--------Init Game Match Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("GameMatchPlayer1");
        context.addUser("GameMatchPlayer2");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);

    } finally {
        context.dispose();
    }
}
