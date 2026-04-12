import WebSocket from "ws";

const endpoint = "http://localhost:8080";
const wspoint = "ws://localhost:8080/ws/game";

class User {
  constructor(nickname, email, password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  setAuth(data) {
    this.id = data.id;
    this.token = data.token;
  }

  updateState(state) {
    const player = state.players.find(p => p.id === this.id);
    if (player) {
      this.playerState = player;
    }
  }
}

const users = [
  new User("Zidan", "zidan@email.com", "12345678"),
  new User("Ronaldo", "ronaldo@email.com", "12345678")
];

const events = [];

async function http(method, path, body) {
  const res = await fetch(`${endpoint}${path}`, {
    method,
    headers: { "Content-Type": "application/json" },
    body: body ? JSON.stringify(body) : undefined
  }).then(res => res.json());

  console.log(res);

  return res;
}

async function registerAndLogin(user) {
  console.log(`\n👤 Criando usuário: ${user.nickname}`);

  await http("POST", "/user", {
    nickname: user.nickname,
    email: user.email,
    password: user.password
  });

  console.log(`🔐 Logando: ${user.nickname}`);

  const login = await http("POST", "/user/login", {
    email: user.email,
    password: user.password
  });

  user.setAuth(login.data);
}

function connect(user) {
  return new Promise((resolve) => {
    const ws = new WebSocket(`${wspoint}?token=${user.token}`);

    ws.on("open", () => {
      console.log(`🟢 ${user.nickname} conectado`);
      resolve(ws);
    });

    ws.on("message", (data) => {
        const msg = JSON.parse(data);

        if (msg.data && msg.data.players) {
            user.updateState(msg.data);
        }

        if (user.nickname == "Zidan") {
            console.log(`📩 [${user.nickname}]`, msg);

            if (msg.data && msg.data.board) {
                const board = msg.data.board;

                const lettersBoard = board.map(row =>
                row.map(cell => cell.letter)
                );

                console.table(lettersBoard);
            }

            events.push({ ...msg, user: user.nickname });
        }
    });

    ws.on("close", () => {
      console.log(`🔴 ${user.nickname} desconectado`);
    });
  });
}

function send(ws, payload) {
  ws.send(JSON.stringify(payload));
}

function waitForEvent(predicate, timeout = 5000) {
  return new Promise((resolve, reject) => {
    const start = Date.now();

    const interval = setInterval(() => {
      const index = events.findIndex(predicate);

      if (index !== -1) {
        const event = events.splice(index, 1)[0];
        clearInterval(interval);
        resolve(event);
      }

      if (Date.now() - start > timeout) {
        clearInterval(interval);
        reject("Timeout esperando evento");
      }
    }, 10);
  });
}

async function runGameFlow(ws1, ws2) {
  console.log("\n🎮 Criando jogo...");

  send(ws1, {
    type: "CREATE_GAME",
    name: "Sala Teste",
    settings: {
      allowSpectators: true,
      privateGame: false
    }
  });

  await waitForEvent(e => e.event === "GAME_CREATED");

  const games = await http("GET", "/game");

  const tokenGameId = games.data.games[0]?.tokenGameId;

  send(ws2, {
    type: "JOIN_GAME",
    tokenGameId
  });

  await waitForEvent(e => e.event === "PARTICIPANT_JOIN");


  send(ws1, {
    type: "START_GAME",
    tokenGameId,
    settings: {
      themeId: "tech",
      gameMode: "NORMAL"
    }
  });

  const started = await waitForEvent(e => e.event === "GAME_STARTED");

  let currentPlayer = started.data.currentTurnPlayerId;

  const positions = [];
  for (let x = 0; x < 10; x++) {
    for (let y = 0; y < 10; y++) {
      positions.push({ x, y });
    }
  }

  while (positions.length > 0) {
    const pos = positions.splice(Math.floor(Math.random() * positions.length), 1)[0];

    const currentWs =
      currentPlayer === users[0].id ? ws1 : ws2;

    send(currentWs, {
      type: "PLAYER_ACTION",
      tokenGameId,
      action: {
        type: "REVEAL",
        position: pos
      }
    });

    const result = await waitForEvent(
      e => e.event === "GAME_OVER" ||
      e.event === "PLAYER_ACTION_RESULT" &&
      e.data.currentTurnPlayerId !== currentPlayer
    );

    if (result.event === "GAME_OVER") {
      console.log("🏁 Fim do jogo!");
      break;
    }
    
    currentPlayer = result.data.currentTurnPlayerId;
    const player = currentPlayer === users[0].id ? users[0] : users[1];

    if (player.playerState?.inventory?.length > 0) {
        console.log("🎒 Poderes:", player.playerState.inventory);
    }

    await sleep(5000);
  }

  console.log("\n✅ Teste finalizado com sucesso");
}

function usePower(ws, tokenGameId, power) {
  console.log(`⚡ Usando poder: ${power.name}`);

  let action = null;

  switch (power.name) {
    case "BLOCK":
      action = {
        type: "BLOCK",
        actionId: power.id,
        position: randomPosition()
      };
      break;

    case "UNBLOCK":
      action = {
        type: "UNBLOCK",
        actionId: power.id,
        position: randomPosition()
      };
      break;

    case "TRAP":
      action = {
        type: "TRAP",
        actionId: power.id,
        position: randomPosition()
      };
      break;
    
    case "DETECT_TRAPS":
        action = {
            actionId: power.id,
            type: "DETECT_TRAPS",
        };
        break;

    case "SPY":
      action = {
        type: "SPY",
        actionId: power.id,
        position: randomPosition()
      };
      break;

    case "FREEZE":
      action = {
        type: "FREEZE",
        actionId: power.id,
        targetPlayerId: null
      };
      break;

    default:
      console.log("❓ Poder desconhecido:", power);
      return;
  }

  send(ws, {
    type: "PLAYER_ACTION",
    tokenGameId,
    action
  });
}

async function main() {
  console.log("🚀 Iniciando testes...");

  for (const user of users) {
    await registerAndLogin(user);
  }

  const ws1 = await connect(users[0]);
  const ws2 = await connect(users[1]);

  await runGameFlow(ws1, ws2);
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

main().catch(console.error);