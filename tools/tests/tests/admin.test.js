import { runFlow } from "../flows/admin.flow.js";
import { AdminTestContext } from "../context/AdminTestsContext.js";

export async function run() {
    console.log("\n--------Init Admin Tests--------\n");

    const context = new AdminTestContext();

    try {
        await context.authAdmins(1);

        await runFlow(context);

    } finally {}
}