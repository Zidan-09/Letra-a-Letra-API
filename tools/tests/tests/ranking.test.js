import { runRankingGame } from "../flows/ranking.flow.js";
import { AuthFlow } from "../flows/auth.flow.js";
import { connect } from "../core/websocket.js";
import { User } from "../models/User.js";
import { TestContext } from "../context/TestsContext.js";

export async function run() {
    console.log("\n--------Init Ranking Tests--------\n");

    const context = new TestContext();

    try {
        context.addUser("RankingPlayer1");
        context.addUser("RankingPlayer2");

        await context.authUsers();
        await context.connectSockets();

        await runRankingGame(context);
    } finally {
        context.dispose();
    }
}