import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/ranking-exit.flow.js";

export async function run() {
    console.log("\n--------Init Ranking Exit Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("RankingExitPlayer");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);

    } finally {
        context.dispose();
    }
}
