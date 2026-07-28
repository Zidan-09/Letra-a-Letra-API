import { runFlow as matchmaking } from "./seed/matchmaking.seed.js";
import {TestContext} from "./context/TestsContext.js";

export async function run() {
    console.log("\n--------Init Matchmaking Seeder--------\n");

    const context = new TestContext();

    try {
        context.addUser("zidan");
        context.addUser("wadawueu");

        await context.authUsers();
        await context.connectSockets();

        await matchmaking(context);

    } finally {
        context.dispose();
    }
}

const tests = [
    run
];

for(const test of tests) {

    console.log("\n========================");

    try{

        await test();

        console.log("✅ OK");

    }catch(e){

        console.error("❌ FAIL");

        console.error(e);

        throw e;
    }
}