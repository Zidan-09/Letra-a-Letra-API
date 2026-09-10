import { runFlow } from "../flows/admin-audit.flow.js";
import { AdminTestContext } from "../context/AdminTestsContext.js";
import { TestContext } from "../context/TestsContext.js";

export async function run() {
    console.log("\n--------Init Audit Tests--------\n");

    const adminContext = new AdminTestContext();
    const playerContext = new TestContext();

    try {
        await adminContext.authAdmins(1);

        playerContext.addUser(`audit-${Date.now()}`);

        await playerContext.authUsers();

        await runFlow(adminContext, playerContext);

    } finally {
        playerContext.dispose();
    }
}
