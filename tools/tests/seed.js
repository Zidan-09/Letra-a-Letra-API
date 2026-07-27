import { run as matchmaking } from "./tests/matchmaking.test.js";
import { run as ranking } from "./tests/ranking.test.js";
import { run as casual } from "./tests/casual.test.js";

const tests = [
    matchmaking,
    ranking,
    casual
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