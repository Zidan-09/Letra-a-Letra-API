import { AuthFlow } from "../flows/auth.flow.js";
import { TestContext } from "../context/TestsContext.js";
import { User } from "../models/User.js";

export async function run() {
    console.log("\n--------Init Profile Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("Zidan");

        await context.authUsers();

    } finally {
        context.dispose();
    }
}