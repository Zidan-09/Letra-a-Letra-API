import { runFlow } from "../flows/shop.flow.js";
import { AdminTestContext } from "../context/AdminTestsContext.js";
import { TestContext } from "../context/TestsContext.js";

export async function run() {
    console.log("\n--------Init Shop Tests--------\n");

    const adminContext = new AdminTestContext();
    const playerContext = new TestContext();

    try {
        await adminContext.authAdmins(1);

        const stamp = Date.now();

        playerContext.addUser(`shop-buyer-${stamp}`);
        playerContext.addUser(`shop-poor-${stamp}`);

        await playerContext.authUsers();

        await runFlow(adminContext, playerContext);

    } finally {
        playerContext.dispose();
    }
}
