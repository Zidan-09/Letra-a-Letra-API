import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/matchmaking.flow.js";

export async function run() {
    console.log("\n--------Init Matchmaking Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("MatchmakingPlayer1");
        context.addUser("MatchmakingPlayer2");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);

    } finally {
        context.dispose();
    }
}