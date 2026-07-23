import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/friends.flow.js";

export async function run() {
    console.log("\n--------Init Friends Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("Zidan");
        context.addUser("PombaoLoro");
        context.addUser("Wadawueu");
        context.addUser("Torugo");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);

    } finally {
        context.dispose();
    }
}