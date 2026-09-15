import { describe, it } from "node:test";
import assert from "node:assert/strict";
import {
    BLOCK_UNLOCK_CLICKS,
    blockedRemainingClicks,
    cellCost,
    chooseAction,
    createAfflictions,
    createMemory,
    createRng,
    getInventory,
    observeResult,
    remainingCost,
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

function board4x4(cells) {
    return [
        [cells[0], cells[1], cells[2], cells[3]],
        [cells[4], cells[5], cells[6], cells[7]],
        [cells[8], cells[9], cells[10], cells[11]],
        [cells[12], cells[13], cells[14], cells[15]]
    ];
}

function patternBoard(cells) {
    return board4x4(cells);
}

function word(value, found = false) {
    return { word: value, found, foundById: null };
}

function gameData({ inventory = [], board = null, words = [], myEffects = [], oppEffects = [] } = {}) {
    return {
        currentTurnPlayerId: BOT,
        players: [
            { id: BOT, nickname: "bot", score: 0, inventory, effects: myEffects },
            { id: OPP, nickname: "opp", score: 0, inventory: [], effects: oppEffects }
        ],
        board: board ?? board2x2([cell(), cell(), cell(), cell()]),
        words
    };
}

function withPower(name, id = `${name}-1`) {
    return [{ id, name }];
}

function blockedCell(remainingClicks, ownerId = OPP) {
    return cell(false, { effect: "BLOCK", ownerId, remainingClicks });
}

function fillerBoard() {
    return Array.from({ length: 16 }, () => cell(true, null, "X"));
}

function emptyWordBoard() {
    return patternBoard(fillerBoard());
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

describe("remainingCost", () => {
    it("custo 0 para célula já resolvida", () => {
        assert.equal(cellCost(cell(true, null, "C")), 0);
    });

    it("custo 1 para célula normal", () => {
        assert.equal(cellCost(cell()), 1);
    });

    it("custo da bloqueada reflete os cliques restantes (3º clique já revela)", () => {
        assert.equal(cellCost(blockedCell(3)), 3);
        assert.equal(cellCost(blockedCell(2)), 2);
        assert.equal(cellCost(blockedCell(1)), 1);
    });

    it("lê remainingAttempts quando remainingClicks está ausente", () => {
        assert.equal(blockedRemainingClicks(cell(false, { effect: "BLOCK", ownerId: OPP, remainingAttempts: 2 })), 2);
    });

    it(`assume ${BLOCK_UNLOCK_CLICKS} cliques quando o efeito não informa o restante`, () => {
        assert.equal(blockedRemainingClicks(cell(false, { effect: "BLOCK", ownerId: OPP })), BLOCK_UNLOCK_CLICKS);
    });

    it("3 letras restantes => custo 3 (ímpar)", () => {
        assert.equal(remainingCost([cell(), cell(), cell()]), 3);
    });

    it("2 letras restantes => custo 2 (par)", () => {
        assert.equal(remainingCost([cell(), cell()]), 2);
    });

    it("1 normal + 1 bloqueada => custo 4 (par)", () => {
        assert.equal(remainingCost([cell(), blockedCell(3)]), 4);
    });

    it("cada bloqueada conta individualmente", () => {
        assert.equal(remainingCost([blockedCell(3), blockedCell(2)]), 5);
    });

    it("só células ainda necessárias entram no cálculo", () => {
        assert.equal(remainingCost([cell(true, null, "C"), cell(), cell(true, null, "A")]), 1);
    });

    it("palavra completa => custo 0", () => {
        assert.equal(remainingCost([]), 0);
        assert.equal(remainingCost([cell(true, null, "C"), cell(true, null, "A")]), 0);
    });
});

describe("chooseAction", () => {
    it("escolhe a célula faltante em um padrão horizontal de CASA", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = cell();
        board[0][2] = cell(true, null, "S");
        board[0][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: createRng(42)
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 0, y: 1 } });
    });

    it("escolhe a célula faltante quando a palavra começa oculta", () => {
        const board = emptyWordBoard();
        board[0][0] = cell();
        board[0][1] = cell(true, null, "A");
        board[0][2] = cell(true, null, "S");
        board[0][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: createRng(42)
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 0, y: 0 } });
    });

    it("ignora palavras já encontradas", () => {
        const board = emptyWordBoard();
        board[0][0] = cell();
        board[0][1] = cell(true, null, "A");
        board[0][2] = cell(true, null, "S");
        board[0][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA", true)] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 0, y: 0 } });
    });

    it("procura padrões horizontais", () => {
        const board = emptyWordBoard();
        board[2][0] = cell(true, null, "C");
        board[2][1] = cell();
        board[2][2] = cell(true, null, "S");
        board[2][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: createRng(42)
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 2, y: 1 } });
    });

    it("procura padrões verticais", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[1][0] = cell();
        board[2][0] = cell(true, null, "S");
        board[3][0] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: createRng(42)
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 1, y: 0 } });
    });

    it("procura padrões diagonais", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[1][1] = cell();
        board[2][2] = cell(true, null, "S");
        board[3][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: createRng(42)
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 1, y: 1 } });
    });

    it("descarta padrões com uma letra revelada diferente", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = cell();
        board[0][2] = cell(true, null, "X");
        board[0][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 0, y: 1 } });
    });

    it("usa fallback aleatório quando não existe nenhum padrão", () => {
        const action = chooseAction({
            gameData: gameData({ words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 0, y: 0 } });
    });

    it("joga quando o custo restante é ímpar (3 letras)", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = cell();
        board[0][2] = cell();
        board[0][3] = cell();
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 0, y: 1 } });
    });

    it("adia a jogada quando o custo restante é par (2 letras)", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = cell();
        board[0][2] = cell();
        board[0][3] = cell(true, null, "A");
        board[3][3] = cell();
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 3, y: 3 } });
    });

    it("prioriza o padrão com menos células faltantes", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = cell();
        board[0][2] = cell();
        board[0][3] = cell(true, null, "A");
        board[1][0] = cell(true, null, "C");
        board[1][1] = cell();
        board[1][2] = cell(true, null, "S");
        board[1][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: createRng(42)
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 1, y: 1 } });
    });

    it("entre candidatos ímpares, prioriza menos células faltantes", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = cell();
        board[0][2] = cell(true, null, "S");
        board[0][3] = cell(true, null, "A");
        board[2][0] = cell(true, null, "C");
        board[2][1] = cell();
        board[2][2] = cell();
        board[2][3] = cell();
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 0, y: 1 } });
    });

    it("não descarta palavra com célula bloqueada (custo ímpar, sem poder)", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = blockedCell(3);
        board[0][2] = cell(true, null, "S");
        board[0][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 0, y: 1 } });
    });

    it("adia palavra com célula bloqueada quando o custo é par", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = cell();
        board[0][2] = blockedCell(3);
        board[0][3] = cell(true, null, "A");
        board[3][3] = cell();
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "REVEAL", position: { x: 3, y: 3 } });
    });

    it("não tenta jogar palavra completamente preenchida", () => {
        const board = emptyWordBoard();
        board[0][0] = cell(true, null, "C");
        board[0][1] = cell(true, null, "A");
        board[0][2] = cell(true, null, "S");
        board[0][3] = cell(true, null, "A");
        const action = chooseAction({
            gameData: gameData({ board, words: [word("CASA")] }),
            myId: BOT,
            memory: createMemory(),
            rng: always
        });

        assert.deepEqual(action, { type: "PASS", reason: "BOARD_EXHAUSTED" });
    });

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
