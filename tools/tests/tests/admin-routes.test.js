import { AdminTestContext } from "../context/AdminTestsContext.js";
import { runFlow as runCosmetic } from "../flows/admin-cosmetic.flow.js";
import { runFlow as runOffers } from "../flows/admin-offers.flow.js";
import { runFlow as runGame } from "../flows/admin-game.flow.js";
import { runFlow as runLevels } from "../flows/admin-levels.flow.js";

export async function run() {
    console.log("\n--------Init Admin Routes Tests--------\n");

    const context = new AdminTestContext();

    try {
            await context.authAdmins(1);
    
            // await runCosmetic(context);
            await runOffers(context);
            await runGame(context);
            await runLevels(context);
    
        } finally {}
}