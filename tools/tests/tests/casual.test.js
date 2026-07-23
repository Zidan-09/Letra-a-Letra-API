import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/casual.flow.js";


export async function run() {
    console.log("\n--------Init Casual Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("CasualPlayer1");
        context.addUser("CasualPlayer2");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);

    } finally {
        context.dispose();
    }
}