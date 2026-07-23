import { TestContext } from "../context/TestsContext.js";
import { runFlow } from "../flows/room.flow.js";

export async function run() {
    const context = new TestContext();

    try {
        context.addUser("Participant1");
        context.addUser("Participant2");
        context.addUser("Participant3");

        await context.authUsers();
        await context.connectSockets();

        await runFlow(context);

    } finally {
        context.dispose();
    }
}