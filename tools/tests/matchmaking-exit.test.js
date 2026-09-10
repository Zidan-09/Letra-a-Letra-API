import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/matchmaking-exit.flow.js";

export async function run() {
    console.log("\n--------Init Matchmaking Exit Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("MatchmakingExitPlayer");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);

    } finally {
        context.dispose();
    }
}
