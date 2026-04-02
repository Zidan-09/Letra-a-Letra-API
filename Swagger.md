
# 📘 API Documentation

## 🔗 Overview

* **Base HTTP URL:** `http://<host>:8080`
* **WebSocket URL:** `ws://<host>:8080/ws/game?token=<JWT>`

### 🔐 Autenticação

* HTTP: não requer autenticação
* WebSocket: requer `token` (JWT) obtido em `/user/login`

---

## 📦 Padrões de Resposta

### HTTP Success

```json
{
  "success": true,
  "message": "ok",
  "data": {}
}
```

### HTTP Error

```json
{
  "success": false,
  "message": "error_code"
}
```

### WebSocket Success

```json
{
  "event": "EVENT_NAME",
  "data": {}
}
```

### WebSocket Error

```json
{
  "event": "ERROR",
  "message": "error_code"
}
```

---

# 🌐 HTTP Endpoints

## 👤 Usuário

### Criar usuário

**POST** `/user`

```json
{
  "nickname": "Zidan",
  "email": "zidan@email.com",
  "password": "12345678"
}
```

---

### Login

**POST** `/user/login`

```json
{
  "email": "zidan@email.com",
  "password": "12345678"
}
```

Resposta:

```json
{
  "success": true,
  "data": {
    "id": "user-id",
    "token": "jwt-token"
  }
}
```

---

### Buscar usuário

**GET** `/user/{userId}`

---

## 🎮 Game

### Listar jogos públicos

**GET** `/game`

---

### Buscar jogo por token

**GET** `/game/{tokenGameId}`

---

### Buscar token por código

**GET** `/game/code/{code}`

---

# 🔌 WebSocket

## 🔑 Conexão

```
ws://<host>:8080/ws/game?token=<JWT>
```

* Token obrigatório
* Se usuário já estiver em jogo → recebe `PARTICIPANT_RECONNECTED`

---

## 📡 Formato das mensagens

### Padrão geral

```json
{
  "type": "ACTION_NAME"
}
```

### Com payload

```json
{
  "type": "ACTION_NAME",
  "tokenGameId": "game-token"
}
```

---

# 🎮 Ações WebSocket

## 🆕 CREATE_GAME

```json
{
  "type": "CREATE_GAME",
  "name": "Minha Sala",
  "settings": {
    "allowSpectators": true,
    "privateGame": false
  }
}
```

Evento:

```json
{
  "event": "GAME_CREATED"
}
```

---

## 🚪 JOIN_GAME

```json
{
  "type": "JOIN_GAME",
  "tokenGameId": "game-token"
}
```

Evento:

```json
{
  "event": "PARTICIPANT_JOIN"
}
```

---

## 🔄 SWAP_POSITION

```json
{
  "type": "SWAP_POSITION",
  "tokenGameId": "game-token",
  "position": 3
}
```

Evento:

```json
{
  "event": "POSITIONS_UPDATED"
}
```

---

## ▶️ START_GAME

```json
{
  "type": "START_GAME",
  "tokenGameId": "game-token",
  "settings": {
    "themeId": "tech",
    "gameMode": "NORMAL"
  }
}
```

Evento:

```json
{
  "event": "GAME_STARTED"
}
```

---

## 🚶 LEFT_GAME

```json
{
  "type": "LEFT_GAME",
  "tokenGameId": "game-token"
}
```

Evento:

```json
{
  "event": "PARTICIPANT_LEAVE"
}
```

---

## ❌ KICK_PARTICIPANT

```json
{
  "type": "KICK_PARTICIPANT",
  "tokenGameId": "game-token",
  "participantId": "user-id"
}
```

Evento:

```json
{
  "event": "PARTICIPANT_KICKED"
}
```

---

## ⛔ BAN_PARTICIPANT

```json
{
  "type": "BAN_PARTICIPANT",
  "tokenGameId": "game-token",
  "participantId": "user-id"
}
```

Evento:

```json
{
  "event": "PARTICIPANT_BANNED"
}
```

---

## ✅ UNBAN_PARTICIPANT

```json
{
  "type": "UNBAN_PARTICIPANT",
  "tokenGameId": "game-token",
  "userId": "user-id"
}
```

Evento:

```json
{
  "event": "PARTICIPANT_UNBANNED"
}
```

---

## 🎯 PLAYER_ACTION (REVEAL)

```json
{
  "type": "PLAYER_ACTION",
  "tokenGameId": "game-token",
  "action": {
    "type": "REVEAL",
    "position": { "x": 0, "y": 1 }
  }
}
```

Evento:

```json
{
  "event": "PLAYER_ACTION_RESULT"
}
```

---

# ⚙️ Eventos Automáticos

## 🔁 PARTICIPANT_RECONNECTED

Recebido ao reconectar no jogo

---

## 🔌 PARTICIPANT_DISCONNECTED

Enviado quando usuário desconecta

---

## ⏳ INACTIVITY

Sala fechada após 5 minutos sem iniciar

---

# ⚠️ Observações importantes

* WebSocket usa **dispatcher baseado em `type`**
* Apenas ação `REVEAL` está implementada no jogo
* Outras ações existem no DTO mas retornam erro
* `privateGame` só afeta listagem
* IDs de jogo são **JWTs**, não IDs reais
