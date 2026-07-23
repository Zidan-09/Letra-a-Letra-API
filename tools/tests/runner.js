import { run as profile } from "./tests/profile.test.js";
import { run as friends } from "./tests/friends.test.js";
import { run as matchmaking } from "./tests/matchmaking.test.js";
import { run as ranking } from "./tests/ranking.test.js";
import { run as casual } from "./tests/casual.test.js";
import { run as room } from "./tests/room.test.js";
import { run as turn } from "./tests/turn.test.js";

const tests = [
    profile,
    friends,
    matchmaking,
    ranking,
    casual,
    room,
    turn
];

for(const test of tests) {

    console.log("\n========================");

    try{

        await test();

        console.log("✅ OK");

    }catch(e){

        console.error("❌ FAIL");

        console.error(e);

    }
}