import WebSocket from "ws";

class User {
  constructor(nickname, email, password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  addToken(token) {
    this.token = token;
  }
}

const endpoint = "http://localhost:8080";
const wspoint = "ws://localhost:8080/ws/game";

const user1 = new User("Zidan", "zidan@email.com", "12345678");
const user2 = new User("Ronaldo", "ronaldo@email.com", "12345678");
const user3 = new User("Rogério", "rogerio@email.com", "12345678");

const events = [];

async function main() {
  for (const user of [user1, user2, user3]) {
    await fetch(`${endpoint}/user`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        nickname: user.nickname,
        email: user.email,
        password: user.password
      })
    });

    const res = await fetch(`${endpoint}/user/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: user.email,
        password: user.password
      })
    }).then(res => res.json());

    user.addToken(res.data.token);
  }
}

function connectUser(user) {
  return new Promise((resolve) => {
    const ws = new WebSocket(`${wspoint}?token=${user.token}`);

    ws.on("open", () => {
      console.log(`${user.nickname} conectado`);
      resolve(ws);
    });

    ws.on("message", (data) => {
      const msg = JSON.parse(data);
      console.log(msg)
      events.push(msg);
    });

    ws.on("close", () => {
      console.log(`${user.nickname} desconectado`);
    });
  });
}

async function startSocket() {
  await main();

  const ws1 = await connectUser(user1);
  const ws2 = await connectUser(user2);
  const ws3 = await connectUser(user3);

  await ws1.send(JSON.stringify({
    type: "CREATE_GAME",
    name: "Minha Sala",
    settings: {
      themeId: "tech",
      gameMode: "NORMAL",
      allowSpectators: true,
      privateGame: false
    }
  }));

  const createEvent = await waitForEvent(e => e.event === "GAME_UPDATED");

  await ws2.send(JSON.stringify({
    type: "JOIN_GAME",
    tokenGameId: createEvent.data.tokenGameId
  }));

  const joinEvent = await waitForEvent(e => e.event === "GAME_UPDATED");

  await ws3.send(JSON.stringify({
    type: "JOIN_GAME",
    tokenGameId: joinEvent.data.tokenGameId
  }));

  const joinEvent2 = await waitForEvent(e => e.event === "GAME_UPDATED");

  await ws3.send(JSON.stringify({
    type: "JOIN_GAME",
    tokenGameId: joinEvent2.data.tokenGameId
  }));

  const err = await waitForEvent(e => e.event === "ERROR");

  await ws1.send(JSON.stringify({
    type: "START_GAME",
    tokenGameId: joinEvent2.data.tokenGameId
  }));

  const startEvent = await waitForEvent(e => e.event === "GAME_STARTED");
}

function waitForEvent(predicate) {
  return new Promise((resolve) => {
    const interval = setInterval(() => {
      const event = events.find(predicate);

      if (event) {
        clearInterval(interval);
        resolve(event);
      }
    }, 10);
  });
}

startSocket();