import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/turn.flow.js";

export async function run() {
    console.log("\n--------Init Turn Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("Turn1");
        context.addUser("Turn2");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);
    } finally {
        context.dispose();
    }
}