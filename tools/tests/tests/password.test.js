import {TestContext} from "../context/TestsContext.js";
import {runFlow} from "../flows/password.flow.js";

export async function run() {
    console.log("\n--------Init Password Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("Esquecido");

        await runFlow(context);

    } finally {
        context.dispose();
    }
}