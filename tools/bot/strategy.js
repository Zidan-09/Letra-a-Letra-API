export const POWER_TYPES = [
    "BLOCK",
    "UNBLOCK",
    "TRAP",
    "DETECT_TRAPS",
    "SPY",
    "FREEZE",
    "UNFREEZE",
    "BLIND",
    "LANTERN",
    "IMMUNITY"
];

export const DETECT_COOLDOWN_TURNS = 10;

export function createRng(seed) {
    let state = (Number(seed) >>> 0) || 0x9e3779b9;
    return function next() {
        state |= 0;
        state = (state + 0x6d2b79f5) | 0;
        let t = Math.imul(state ^ (state >>> 15), 1 | state);
        t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
        return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
    };
}

export function pickRandom(rng, items) {
    if (!items || items.length === 0) return undefined;
    return items[Math.floor(rng() * items.length)];
}

export function getPlayers(gameData) {
    return gameData?.players ?? [];
}

export function getMyPlayer(gameData, myId) {
    return getPlayers(gameData).find(p => p.id === myId);
}

export function getOpponents(gameData, myId) {
    return getPlayers(gameData).filter(p => p.id !== myId);
}

export function getInventory(gameData, myId) {
    return getMyPlayer(gameData, myId)?.inventory ?? [];
}

export function takePower(inventory, name) {
    return (inventory ?? []).find(item => item?.name === name);
}

const DIRECTIONS = [
    [1, 0],
    [-1, 0],
    [0, 1],
    [0, -1],
    [1, 1],
    [1, -1],
    [-1, 1],
    [-1, -1]
];

function normalizeLetter(value) {
    return String(value ?? "").trim().toUpperCase();
}

function normalizeWord(value) {
    return normalizeLetter(value);
}

function getCell(board, x, y) {
    if (!Array.isArray(board) || !Array.isArray(board[x])) {
        return undefined;
    }

    return board[x][y];
}

function isAvailableCell(cell) {
    return Boolean(cell) && !cell.revealed && !cell.effect;
}

function findPatternForWord(word, board, x, y, dx, dy) {
    const target = normalizeWord(word?.word);
    if (!target) return null;

    const missingCells = [];
    for (let i = 0; i < target.length; i++) {
        const cell = getCell(board, x + dx * i, y + dy * i);
        if (!cell) return null;

        if (cell.revealed) {
            if (normalizeLetter(cell.letter) !== target[i]) return null;
            continue;
        }

        if (!isAvailableCell(cell)) return null;
        missingCells.push({ x: x + dx * i, y: y + dy * i });
    }

    if (missingCells.length === 0 || missingCells.length === target.length) return null;

    return {
        word: target,
        missingCells,
        position: missingCells[0]
    };
}

function addBestCandidate(candidates, pattern) {
    const key = `${pattern.position.x},${pattern.position.y}`;
    if (!candidates.some(candidate =>
        `${candidate.position.x},${candidate.position.y}` === key)) {
        candidates.push(pattern);
    }
}

function findBestPattern(gameData, rng) {
    const board = gameData?.board;
    const words = Array.isArray(gameData?.words) ? gameData.words : [];
    let best = null;
    const candidates = [];

    for (const word of words) {
        if (word?.found === true) continue;

        for (let x = 0; x < board?.length ?? 0; x++) {
            const row = board?.[x];
            if (!Array.isArray(row)) continue;

            for (let y = 0; y < row.length; y++) {
                for (const [dx, dy] of DIRECTIONS) {
                    const pattern = findPatternForWord(word, board, x, y, dx, dy);
                    if (!pattern) continue;

                    if (!best || pattern.missingCells.length < best.missingCells.length) {
                        best = pattern;
                        candidates.length = 0;
                        candidates.push(pattern);
                    } else if (pattern.missingCells.length === best.missingCells.length) {
                        addBestCandidate(candidates, pattern);
                    }
                }
            }
        }
    }

    if (candidates.length === 0) return null;
    return pickRandom(rng ?? Math.random, candidates);
}

export function scanBoard(board) {
    const unrevealed = [];
    const blocked = [];
    if (!Array.isArray(board)) return { unrevealed, blocked };

    for (let x = 0; x < board.length; x++) {
        const row = board[x];
        if (!Array.isArray(row)) continue;
        for (let y = 0; y < row.length; y++) {
            const cell = row[y];
            if (!cell || cell.revealed) continue;
            const kind = cell.effect?.effect ?? null;
            const entry = { x, y, effectKind: kind, ownerId: cell.effect?.ownerId ?? null };
            unrevealed.push(entry);
            if (kind === "BLOCK") blocked.push(entry);
        }
    }
    return { unrevealed, blocked };
}

export function createAfflictions() {
    return { frozen: new Set(), blind: new Set(), immune: new Set(), detecting: new Set() };
}

function collectIds(value, into) {
    if (typeof value === "string" && value.length > 0) {
        into.push(value);
        return;
    }
    if (Array.isArray(value)) {
        for (const v of value) collectIds(v, into);
        return;
    }
    if (value && typeof value === "object") {
        if (typeof value.x === "number" && typeof value.y === "number") return;
        for (const v of Object.values(value)) collectIds(v, into);
    }
}

export function idsInEventData(data) {
    const ids = [];
    collectIds(data, ids);
    return [...new Set(ids)];
}

export function updateAfflictions(afflictions, events) {
    if (!Array.isArray(events)) return afflictions;
    for (const wrapper of events) {
        const name = wrapper?.event;
        const ids = idsInEventData(wrapper?.data);
        switch (name) {
            case "PLAYER_FROZEN":
                ids.forEach(id => afflictions.frozen.add(id));
                break;
            case "PLAYER_UNFREEZE":
                ids.forEach(id => afflictions.frozen.delete(id));
                break;
            case "PLAYER_BLINDED":
                ids.forEach(id => afflictions.blind.add(id));
                break;
            case "PLAYER_USE_LANTERN":
                ids.forEach(id => afflictions.blind.delete(id));
                break;
            case "PLAYER_USE_IMMUNITY":
                ids.forEach(id => {
                    afflictions.frozen.delete(id);
                    afflictions.blind.delete(id);
                    afflictions.immune.add(id);
                });
                break;
            case "PLAYER_ARE_IMMUNE":
                ids.forEach(id => afflictions.immune.add(id));
                break;
            case "TRAPS_DETECTED":
                ids.forEach(id => afflictions.detecting.add(id));
                break;
            default:
                break;
        }
    }
    return afflictions;
}

export function clearExpiredAfflictions(afflictions, gameData, myId) {
    const me = getMyPlayer(gameData, myId);
    if (me && Array.isArray(me.effects) && me.effects.length === 0) {
        afflictions.frozen.delete(myId);
        afflictions.blind.delete(myId);
        afflictions.immune.delete(myId);
        afflictions.detecting.delete(myId);
    }
    return afflictions;
}

export function createMemory() {
    return {
        afflictions: createAfflictions(),
        spied: null,
        turns: 0,
        lastDetectTurn: null
    };
}

function usePowerAction(type, item, extra = {}) {
    return { type, actionId: item.id, ...extra };
}

export function chooseAction({ gameData, myId, memory, rng, powerChance = 0.4 }) {
    const roll = rng ?? Math.random;
    const mem = memory ?? createMemory();
    const inventory = getInventory(gameData, myId);
    const aff = mem.afflictions;
    const { unrevealed, blocked } = scanBoard(gameData?.board);
    const opponents = getOpponents(gameData, myId);
    const target = opponents[0];

    const isFrozen = aff.frozen.has(myId);
    if (isFrozen) {
        const unfreeze = takePower(inventory, "UNFREEZE");
        if (unfreeze) return usePowerAction("UNFREEZE", unfreeze);
        const immunity = takePower(inventory, "IMMUNITY");
        if (immunity) return usePowerAction("IMMUNITY", immunity);
        return { type: "PASS", reason: "FROZEN_WITHOUT_CURE" };
    }

    const isBlind = aff.blind.has(myId);
    if (isBlind) {
        const lantern = takePower(inventory, "LANTERN");
        if (lantern) return usePowerAction("LANTERN", lantern);
        const immunity = takePower(inventory, "IMMUNITY");
        if (immunity) return usePowerAction("IMMUNITY", immunity);
    }

    const unblock = takePower(inventory, "UNBLOCK");
    if (unblock && blocked.length > 0) {
        const enemyBlocked = blocked.filter(c => c.ownerId !== myId);
        const cell = enemyBlocked[0] ?? blocked[0];
        return usePowerAction("UNBLOCK", unblock, { position: { x: cell.x, y: cell.y } });
    }

    const detect = takePower(inventory, "DETECT_TRAPS");
    const detectDue = mem.lastDetectTurn === null || mem.turns - mem.lastDetectTurn >= DETECT_COOLDOWN_TURNS;
    if (detect && detectDue && !aff.detecting.has(myId)) {
        return usePowerAction("DETECT_TRAPS", detect);
    }

    const freeCells = unrevealed.filter(c => !c.effectKind);
    const spy = takePower(inventory, "SPY");
    if (spy && freeCells.length > 0 && roll() < powerChance) {
        const cell = pickRandom(roll, freeCells);
        return usePowerAction("SPY", spy, { position: { x: cell.x, y: cell.y } });
    }

    if (target) {
        const freeze = takePower(inventory, "FREEZE");
        if (freeze && !aff.frozen.has(target.id) && !aff.immune.has(target.id) && roll() < powerChance) {
            return usePowerAction("FREEZE", freeze, { targetId: target.id });
        }
        const blind = takePower(inventory, "BLIND");
        if (blind && !aff.blind.has(target.id) && !aff.immune.has(target.id) && roll() < powerChance) {
            return usePowerAction("BLIND", blind, { targetId: target.id });
        }
    }

    const trap = takePower(inventory, "TRAP");
    if (trap && freeCells.length > 0 && roll() < powerChance) {
        const cell = pickRandom(roll, freeCells);
        return usePowerAction("TRAP", trap, { position: { x: cell.x, y: cell.y } });
    }

    const block = takePower(inventory, "BLOCK");
    if (block && freeCells.length > 0 && roll() < powerChance) {
        const cell = pickRandom(roll, freeCells);
        return usePowerAction("BLOCK", block, { position: { x: cell.x, y: cell.y } });
    }

    const pattern = findBestPattern(gameData, roll);
    if (pattern) {
        return { type: "REVEAL", position: pattern.position };
    }

    if (mem.spied) {
        const stillThere = unrevealed.find(c => c.x === mem.spied.x && c.y === mem.spied.y && !c.effectKind);
        if (stillThere) {
            return { type: "REVEAL", position: { x: stillThere.x, y: stillThere.y } };
        }
    }

    if (freeCells.length > 0) {
        const cell = pickRandom(roll, freeCells);
        return { type: "REVEAL", position: { x: cell.x, y: cell.y } };
    }

    if (unrevealed.length > 0) {
        const cell = pickRandom(roll, unrevealed);
        return { type: "REVEAL", position: { x: cell.x, y: cell.y } };
    }

    return { type: "PASS", reason: "BOARD_EXHAUSTED" };
}

export function toPayload(gameId, action) {
    if (!action || action.type === "PASS") return null;
    const inner = { type: action.type };
    if (action.actionId) inner.actionId = action.actionId;
    if (action.position) inner.position = action.position;
    if (action.targetId) inner.targetId = action.targetId;
    return { type: "PLAYER_ACTION", gameId, action: inner };
}

export function observeResult(memory, action, myId = null) {
    memory.turns += 1;
    if (action?.type === "SPY" && action.position) {
        memory.spied = { ...action.position };
    }
    if (action?.type === "REVEAL" && memory.spied &&
        action.position?.x === memory.spied.x && action.position?.y === memory.spied.y) {
        memory.spied = null;
    }
    if (action?.type === "DETECT_TRAPS") {
        memory.lastDetectTurn = memory.turns;
        if (myId) memory.afflictions.detecting.add(myId);
    }
    if (action?.type === "UNFREEZE" && myId) {
        memory.afflictions.frozen.delete(myId);
    }
    if (action?.type === "IMMUNITY" && myId) {
        memory.afflictions.frozen.delete(myId);
        memory.afflictions.blind.delete(myId);
        memory.afflictions.immune.add(myId);
    }
    if (action?.type === "LANTERN" && myId) {
        memory.afflictions.blind.delete(myId);
    }
    return memory;
}
