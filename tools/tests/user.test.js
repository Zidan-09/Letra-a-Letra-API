import { runFlow } from "../flows/user.flow.js";
import { AdminTestContext } from "../context/AdminTestsContext.js";
import { TestContext } from "../context/TestsContext.js";

export async function run() {
    console.log("\n--------Init User Tests--------\n");

    const adminContext = new AdminTestContext();
    const playerContext = new TestContext();

    try {
        await adminContext.authAdmins(1);

        const stamp = Date.now();

        playerContext.addUser(`user-${stamp}`);
        playerContext.addUser(`user2-${stamp}`);
        playerContext.addUser(`ban-${stamp}`);
        playerContext.addUser(`reset-${stamp}`);

        await playerContext.authUsers();

        await runFlow(adminContext, playerContext);

    } finally {
        playerContext.dispose();
    }
}
