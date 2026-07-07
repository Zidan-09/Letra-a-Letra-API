import WebSocket from "ws";

const endpoint = "http://localhost:8080";
const wspoint = "ws://localhost:8080/ws/game";
const runId = Date.now();

class User {
  constructor(nickname) {
    this.nickname = `${nickname}${runId}`;
    this.email = `${nickname.toLowerCase()}${runId}@email.com`;
    this.password = "12345678";
  }

  setAuth(data) {
    this.id = data.id;
    this.token = data.token;
  }
}

const users = [
  new User("Alice"),
  new User("Bob"),
  new User("Carol"),
  new User("Dave")
];

const events = [];

/* =========================
   ASSERTIONS
========================= */

function assert(condition, message, data = undefined) {
  if (!condition) {
    console.error("\nFalha no teste:", message);

    if (data !== undefined) {
      console.error(JSON.stringify(data, null, 2));
    }

    throw new Error(message);
  }
}

function assertResponseOkNoContent(response, context) {
  assert(response.ok, `${context} deveria retornar HTTP 2xx`, response);
}

function assertResponseOk(response, context) {
  assert(response.ok, `${context} deveria retornar HTTP 2xx`, response);
  assert(response.body, `${context} deveria retornar body`, response);
}

function assertResponseError(response, context) {
  assert(!response.ok, `${context} deveria retornar erro HTTP`, response);
}

function getList(response, key) {
  return response.body?.data?.[key] ?? [];
}

function hasFriendWithStatus(list, userA, userB, status) {
  return list.some(item =>
    item.status === status &&
    (
      item.userId1 === userA.id && item.userId2 === userB.id ||
      item.userId1 === userB.id && item.userId2 === userA.id
    )
  );
}

/* =========================
   HTTP HELPERS
========================= */

async function http(method, path, body, token = undefined) {
  const headers = { "Content-Type": "application/json" };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const res = await fetch(`${endpoint}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });

  const text = await res.text();
  const json = text ? JSON.parse(text) : null;
  const response = {
    ok: res.ok,
    status: res.status,
    body: json
  };

  console.log(`\n${method} ${path} -> ${res.status}`);
  console.log(json);

  return response;
}

/* =========================
   AUTH FLOW
========================= */

async function registerAndLogin(user) {
  console.log(`\nCriando usuario: ${user.nickname}`);

  const create = await http("POST", "/user", {
    nickname: user.nickname,
    email: user.email,
    password: user.password
  });

  assertResponseOk(create, `Criacao do usuario ${user.nickname}`);

  console.log(`Logando: ${user.nickname}`);

  const login = await http("POST", "/user/auth", {
    email: user.email,
    password: user.password
  });

  assertResponseOk(login, `Login do usuario ${user.nickname}`);
  assert(login.body.data?.id, "Login deveria retornar id do usuario", login);
  assert(login.body.data?.token, "Login deveria retornar token", login);

  user.setAuth(login.body.data);
}

/* =========================
   WEBSOCKET
========================= */

function connect(user) {
  return new Promise((resolve, reject) => {
    const ws = new WebSocket(`${wspoint}?token=${user.token}`);
    const timeout = setTimeout(() => reject(new Error(`Timeout conectando ${user.nickname}`)), 5000);

    ws.on("open", () => {
      clearTimeout(timeout);
      console.log(`Socket conectado: ${user.nickname}`);
      resolve(ws);
    });

    ws.on("message", (data) => {
      const msg = JSON.parse(data);

      console.log(`[WS ${user.nickname}]`, msg);
      events.push({ ...msg, user: user.nickname, userId: user.id });
    });

    ws.on("error", (err) => {
      clearTimeout(timeout);
      reject(err);
    });

    ws.on("close", () => {
      console.log(`Socket desconectado: ${user.nickname}`);
    });
  });
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
        reject(new Error("Timeout esperando evento websocket"));
      }
    }, 10);
  });
}

async function expectNoEvent(predicate, timeout = 800) {
  try {
    const event = await waitForEvent(predicate, timeout);
    assert(false, "Evento websocket inesperado", event);
  } catch (err) {
    if (err.message !== "Timeout esperando evento websocket") {
      throw err;
    }
  }
}

function closeSockets(sockets) {
  for (const socket of sockets) {
    if (socket?.readyState === WebSocket.OPEN) {
      socket.close();
    }
  }
}

/* =========================
   FRIEND FLOW
========================= */

async function sendFriendRequest(sender, receiver) {
  const response = await http("POST", "/friend/request", {
    friendId: receiver.id
  }, sender.token);

  assertResponseOk(response, `${sender.nickname} enviando solicitacao para ${receiver.nickname}`);
  assert(response.body.data?.request, "Resposta de solicitacao deveria conter data.request", response);
  assert(response.body.data.request.userId1 === sender.id, "Solicitacao deveria salvar o remetente em userId1", response);
  assert(response.body.data.request.userId2 === receiver.id, "Solicitacao deveria salvar o destinatario em userId2", response);
  assert(response.body.data.request.status === "PENDING", "Solicitacao deveria iniciar como PENDING", response);

  return response;
}

async function assertPendingRequest(receiver, sender) {
  const pending = await http("GET", "/friend/pending", undefined, receiver.token);

  assertResponseOk(pending, `${receiver.nickname} buscando solicitacoes pendentes`);
  assert(
    hasFriendWithStatus(getList(pending, "requests"), sender, receiver, "PENDING"),
    `${receiver.nickname} deveria ver solicitacao pendente de ${sender.nickname}`,
    pending
  );
}

async function assertEmptyPending(receiver, sender) {
  const pending = await http("GET", "/friend/pending", undefined, receiver.token);

  assertResponseOk(pending, `${receiver.nickname} buscando solicitacoes pendentes vazias`);
  assert(
    !hasFriendWithStatus(getList(pending, "requests"), sender, receiver, "PENDING"),
    `${receiver.nickname} nao deveria mais ver solicitacao pendente de ${sender.nickname}`,
    pending
  );
}

async function assertFriendship(userA, userB) {
  const friendsA = await http("GET", "/friend", undefined, userA.token);
  const friendsB = await http("GET", "/friend", undefined, userB.token);

  assertResponseOk(friendsA, `${userA.nickname} buscando lista de amigos`);
  assertResponseOk(friendsB, `${userB.nickname} buscando lista de amigos`);
  assert(
    hasFriendWithStatus(getList(friendsA, "friends"), userA, userB, "ACCEPT"),
    `${userA.nickname} deveria listar ${userB.nickname} como amigo`,
    friendsA
  );
  assert(
    hasFriendWithStatus(getList(friendsB, "friends"), userA, userB, "ACCEPT"),
    `${userB.nickname} deveria listar ${userA.nickname} como amigo`,
    friendsB
  );
}

async function assertNoFriendship(userA, userB) {
  const friendsA = await http("GET", "/friend", undefined, userA.token);
  const friendsB = await http("GET", "/friend", undefined, userB.token);

  assertResponseOk(friendsA, `${userA.nickname} buscando lista sem amizade`);
  assertResponseOk(friendsB, `${userB.nickname} buscando lista sem amizade`);
  assert(
    !hasFriendWithStatus(getList(friendsA, "friends"), userA, userB, "ACCEPT"),
    `${userA.nickname} nao deveria listar ${userB.nickname} como amigo`,
    friendsA
  );
  assert(
    !hasFriendWithStatus(getList(friendsB, "friends"), userA, userB, "ACCEPT"),
    `${userB.nickname} nao deveria listar ${userA.nickname} como amigo`,
    friendsB
  );
}

async function runFriendFlow() {
  const [alice, bob, carol, dave] = users;

  const aliceWs = await connect(alice);
  const bobWs = await connect(bob);
  const carolWs = await connect(carol);
  const daveWs = await connect(dave);
  const sockets = [aliceWs, bobWs, carolWs, daveWs];

  try {
    console.log("\nFluxo 1: solicitar amizade e notificar via websocket");

    await sendFriendRequest(alice, bob);

    const notification = await waitForEvent(e =>
      e.event === "RECEIVE_FRIEND_REQUEST"
    );

    assert(notification.user === bob.nickname, "Notificacao deveria chegar apenas ao destinatario", notification);
    await expectNoEvent(e => e.event === "RECEIVE_FRIEND_REQUEST" && e.userId !== bob.id);
    await assertPendingRequest(bob, alice);

    console.log("\nFluxo 2: bloquear duplicidade e aceitar somente pelo destinatario");

    const duplicate = await http("POST", "/friend/request", {
      friendId: bob.id
    }, alice.token);

    assertResponseError(duplicate, "Solicitacao duplicada ainda pendente");

    const invalidAccept = await http("PATCH", "/friend/accept", {
      friendId: bob.id
    }, alice.token);

    assertResponseError(invalidAccept, "Remetente tentando aceitar a propria solicitacao");

    const accept = await http("PATCH", "/friend/accept", {
      friendId: alice.id
    }, bob.token);

    assertResponseOkNoContent(accept, "Destinatario aceitando solicitacao");
    await assertEmptyPending(bob, alice);
    await assertFriendship(alice, bob);

    console.log("\nFluxo 3: nao permitir nova solicitacao quando ja sao amigos");

    const requestAcceptedFriend = await http("POST", "/friend/request", {
      friendId: bob.id
    }, alice.token);

    assertResponseError(requestAcceptedFriend, "Solicitacao para amigo ja aceito");

    console.log("\nFluxo 4: remover amizade dos dois lados");

    const remove = await http("PATCH", "/friend/remove", {
      friendId: bob.id
    }, alice.token);

    assertResponseOk(remove, "Remocao de amizade");
    await assertNoFriendship(alice, bob);

    const removeAgain = await http("PATCH", "/friend/remove", {
      friendId: bob.id
    }, alice.token);

    assertResponseError(removeAgain, "Remocao repetida de amizade");

    console.log("\nFluxo 5: recusar solicitacao e permitir reenviar depois da recusa");

    await sendFriendRequest(carol, dave);
    await waitForEvent(e => e.event === "RECEIVE_FRIEND_REQUEST");
    await assertPendingRequest(dave, carol);

    const invalidReject = await http("PATCH", "/friend/reject", {
      friendId: dave.id
    }, carol.token);

    assertResponseError(invalidReject, "Remetente tentando recusar a propria solicitacao");

    const reject = await http("PATCH", "/friend/reject", {
      friendId: carol.id
    }, dave.token);

    assertResponseOk(reject, "Destinatario recusando solicitacao");
    await assertEmptyPending(dave, carol);
    await assertNoFriendship(carol, dave);

    await sendFriendRequest(carol, dave);
    await waitForEvent(e => e.event === "RECEIVE_FRIEND_REQUEST");
    await assertPendingRequest(dave, carol);

    console.log("\nTeste da feature friend finalizado com sucesso");
  } finally {
    closeSockets(sockets);
  }
}

/* =========================
   MAIN
========================= */

async function main() {
  console.log("Iniciando testes da feature friend...");

  for (const user of users) {
    await registerAndLogin(user);
  }

  await runFriendFlow();
}

main().catch(err => {
  console.error(err);
  process.exit(1);
});
