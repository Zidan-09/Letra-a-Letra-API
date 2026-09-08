import { describe, it } from "node:test";
import assert from "node:assert/strict";
import {
    chooseAction,
    createAfflictions,
    createMemory,
    createRng,
    getInventory,
    observeResult,
    scanBoard,
    takePower,
    toPayload,
    updateAfflictions
} from "./strategy.js";

const BOT = "bot-id";
const OPP = "opp-id";

function cell(revealed = false, effect = null, letter = null) {
    return { revealed, letter, revealedBy: revealed ? BOT : null, effect };
}

function board2x2(cells) {
    return [
        [cells[0], cells[1]],
        [cells[2], cells[3]]
    ];
}

function gameData({ inventory = [], board = null, myEffects = [], oppEffects = [] } = {}) {
    return {
        currentTurnPlayerId: BOT,
        players: [
            { id: BOT, nickname: "bot", score: 0, inventory, effects: myEffects },
            { id: OPP, nickname: "opp", score: 0, inventory: [], effects: oppEffects }
        ],
        board: board ?? board2x2([cell(), cell(), cell(), cell()]),
        words: []
    };
}

function withPower(name, id = `${name}-1`) {
    return [{ id, name }];
}

const always = () => 0;

describe("inventory", () => {
    it("reconhece os poderes que o bot possui", () => {
        const data = gameData({ inventory: [...withPower("FREEZE", "f1"), ...withPower("BLOCK", "b1")] });
        const inv = getInventory(data, BOT);
        assert.equal(inv.length, 2);
        assert.equal(takePower(inv, "FREEZE").id, "f1");
        assert.equal(takePower(inv, "LANTERN"), undefined);
    });
});

describe("scanBoard", () => {
    it("separa células ocultas e bloqueadas", () => {
        const board = board2x2([
            cell(true),
            cell(false, { effect: "BLOCK", ownerId: OPP, remainingClicks: 3 }),
            cell(false, { effect: "TRAP", ownerId: BOT }),
            cell()
        ]);
        const { unrevealed, blocked } = scanBoard(board);
        assert.equal(unrevealed.length, 3);
        assert.equal(blocked.length, 1);
        assert.deepEqual({ x: blocked[0].x, y: blocked[0].y }, { x: 0, y: 1 });
        assert.equal(blocked[0].ownerId, OPP);
    });
});

describe("updateAfflictions", () => {
    it("rastreia freeze/blind/immunity/curas via eventos tipados", () => {
        const aff = createAfflictions();
        updateAfflictions(aff, [
            { event: "PLAYER_FROZEN", data: { playerFrozen: BOT } },
            { event: "PLAYER_BLINDED", data: { playerBlinded: OPP } }
        ]);
        assert.ok(aff.frozen.has(BOT));
        assert.ok(aff.blind.has(OPP));

        updateAfflictions(aff, [
            { event: "PLAYER_UNFREEZE", data: { playerUnfreeze: BOT } },
            { event: "PLAYER_USE_IMMUNITY", data: { playerUseImmunity: OPP } }
        ]);
        assert.ok(!aff.frozen.has(BOT));
        assert.ok(!aff.blind.has(OPP));
        assert.ok(aff.immune.has(OPP));
    });
});

describe("chooseAction", () => {
    it("usa REVEAL com posição válida quando não há poderes", () => {
        const action = chooseAction({
            gameData: gameData(),
            myId: BOT,
            memory: createMemory(),
            rng: createRng(42)
        });
        assert.equal(action.type, "REVEAL");
        assert.ok(action.position.x >= 0 && action.position.x < 2);
        assert.ok(action.position.y >= 0 && action.position.y < 2);
    });

    it("cura freeze com UNFREEZE antes de qualquer outra ação", () => {
        const memory = createMemory();
        memory.afflictions.frozen.add(BOT);
        const data = gameData({ inventory: [...withPower("UNFREEZE", "u1"), ...withPower("BLOCK", "b1")] });
        const action = chooseAction({ gameData: data, myId: BOT, memory, rng: always });
        assert.equal(action.type, "UNFREEZE");
        assert.equal(action.actionId, "u1");
    });

    it("usa IMMUNITY quando congelado sem UNFREEZE", () => {
        const memory = createMemory();
        memory.afflictions.frozen.add(BOT);
        const action = chooseAction({
            gameData: gameData({ inventory: withPower("IMMUNITY", "i1") }),
            myId: BOT, memory, rng: always
        });
        assert.equal(action.type, "IMMUNITY");
    });

    it("aguarda (PASS) congelado e sem cura, sem tentar burlar validação", () => {
        const memory = createMemory();
        memory.afflictions.frozen.add(BOT);
        const action = chooseAction({
            gameData: gameData({ inventory: withPower("BLOCK", "b1") }),
            myId: BOT, memory, rng: always
        });
        assert.equal(action.type, "PASS");
    });

    it("cura blind com LANTERN", () => {
        const memory = createMemory();
        memory.afflictions.blind.add(BOT);
        const action = chooseAction({
            gameData: gameData({ inventory: withPower("LANTERN", "l1") }),
            myId: BOT, memory, rng: always
        });
        assert.equal(action.type, "LANTERN");
        assert.equal(action.actionId, "l1");
    });

    it("usa UNBLOCK em célula com BLOCK do oponente", () => {
        const board = board2x2([
            cell(false, { effect: "BLOCK", ownerId: OPP, remainingClicks: 2 }),
            cell(), cell(), cell()
        ]);
        const action = chooseAction({
            gameData: gameData({ inventory: withPower("UNBLOCK", "ub1"), board }),
            myId: BOT, memory: createMemory(), rng: always
        });
        assert.equal(action.type, "UNBLOCK");
        assert.equal(action.actionId, "ub1");
        assert.deepEqual(action.position, { x: 0, y: 0 });
    });

    it("não usa FREEZE contra oponente já congelado", () => {
        const memory = createMemory();
        memory.afflictions.frozen.add(OPP);
        const action = chooseAction({
            gameData: gameData({ inventory: withPower("FREEZE", "f1") }),
            myId: BOT, memory, rng: () => 0.99
        });
        assert.equal(action.type, "REVEAL");
    });

    it("não usa BLIND contra oponente imune", () => {
        const memory = createMemory();
        memory.afflictions.immune.add(OPP);
        const action = chooseAction({
            gameData: gameData({ inventory: withPower("BLIND", "bl1") }),
            myId: BOT, memory, rng: () => 0.99
        });
        assert.equal(action.type, "REVEAL");
    });

    it("usa FREEZE com targetId do oponente quando aplicável", () => {
        const action = chooseAction({
            gameData: gameData({ inventory: withPower("FREEZE", "f1") }),
            myId: BOT, memory: createMemory(), rng: () => 0
        });
        assert.equal(action.type, "FREEZE");
        assert.equal(action.actionId, "f1");
        assert.equal(action.targetId, OPP);
    });

    it("usa DETECT_TRAPS com apenas actionId", () => {
        const action = chooseAction({
            gameData: gameData({ inventory: withPower("DETECT_TRAPS", "d1") }),
            myId: BOT, memory: createMemory(), rng: always
        });
        assert.equal(action.type, "DETECT_TRAPS");
        assert.equal(action.actionId, "d1");
        assert.equal(action.position, undefined);
        assert.equal(action.targetId, undefined);
    });

    it("revela a posição espiada no turno seguinte", () => {
        const memory = createMemory();
        observeResult(memory, { type: "SPY", position: { x: 1, y: 1 } }, BOT);
        const action = chooseAction({
            gameData: gameData(),
            myId: BOT, memory, rng: always
        });
        assert.equal(action.type, "REVEAL");
        assert.deepEqual(action.position, { x: 1, y: 1 });
    });

    it("é determinístico com a mesma seed", () => {
        const data = gameData({ inventory: withPower("TRAP", "t1") });
        const first = chooseAction({ gameData: data, myId: BOT, memory: createMemory(), rng: createRng(7), powerChance: 1 });
        const second = chooseAction({ gameData: data, myId: BOT, memory: createMemory(), rng: createRng(7), powerChance: 1 });
        assert.deepEqual(first, second);
    });
});

describe("toPayload", () => {
    it("monta o envelope PLAYER_ACTION igual ao de um jogador real", () => {
        const payload = toPayload("game-1", { type: "BLOCK", actionId: "b1", position: { x: 1, y: 2 } });
        assert.deepEqual(payload, {
            type: "PLAYER_ACTION",
            gameId: "game-1",
            action: { type: "BLOCK", actionId: "b1", position: { x: 1, y: 2 } }
        });
    });

    it("retorna null para PASS (não envia nada quando deve aguardar)", () => {
        assert.equal(toPayload("game-1", { type: "PASS", reason: "X" }), null);
    });
});
