import { run as profile } from "./tests/profile.test.js";
import { run as friends } from "./tests/friends.test.js";
import { run as matchmaking } from "./tests/matchmaking.test.js";
import { run as ranking } from "./tests/ranking.test.js";
import { run as casual } from "./tests/casual.test.js";
import { run as room } from "./tests/room.test.js";
import { run as admin } from "./tests/admin.test.js";
import { run as audit } from "./tests/audit.test.js";
import { run as adminRoutes } from "./tests/admin-routes.test.js";
import { run as turn } from "./tests/turn.test.js";
import { run as gameLobby } from "./tests/game-lobby.test.js";
import { run as matchmakingExit } from "./tests/matchmaking-exit.test.js";
import { run as rankingExit } from "./tests/ranking-exit.test.js";
import { run as gameMatch } from "./tests/game-match.test.js";

const tests = [
    adminRoutes,
    admin,
    audit,
    profile,
    friends,
    matchmaking,
    ranking,
    casual,
    room,
    gameLobby,
    matchmakingExit,
    rankingExit,
    gameMatch,
    turn,
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