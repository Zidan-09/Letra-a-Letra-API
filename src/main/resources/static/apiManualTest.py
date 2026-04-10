import websockets
import requests
import asyncio
import json

httpUrl = "http://localhost:8080"
wsUrl = "ws://localhost:8080/ws/game?token="

class User:
    def __init__(self, nickname, email, password):
        self.nickname = nickname
        self.email = email
        self.password = password
        self.gameTokenId = None

    def addId(self, id):
        self.id = id

    def addToken(self, token):
        self.token = token


async def listen_server(connection):
    while True:
        try:
            response = await connection.recv()
            data = json.loads(response)

            print(json.dumps(data, indent=4, ensure_ascii=False))

            if "tokenGameId" in data:
                user.gameTokenId = data["data"]["tokenGameId"]
                print("🎮 Game token recebido:", user.gameTokenId)

            if "currentTurnPlayerId" in data.get("data", {}):
                current = data["data"]["currentTurnPlayerId"]

                if current == user.id:
                    print("👉 Sua vez!")

        except Exception as e:
            print("Erro real:", e)
            break


async def send_input(connection, gameToken):
    while True:
        try:
            loop = asyncio.get_event_loop()
            x, y = await loop.run_in_executor(None, input, "Digite x y: ")

            message = {
                "type": "PLAYER_ACTION",
                "tokenGameId": gameToken,
                "action": {
                    "type": "REVEAL",
                    "position": {
                        "x": x,
                        "y": y
                    }
                }
            }

            await connection.send(json.dumps(message))

        except Exception as e:
            print("Erro:", e)


async def connect(user: User):
    async with websockets.connect(f"{wsUrl}{user.token}") as connection:
        asyncio.create_task(listen_server(connection))

        quick_match = {
            "type": "MATCHMAKING_GAME",
            "gameMode": "NORMAL"
        }

        await connection.send(json.dumps(quick_match))

        print("🎮 Procurando partida...")

        while user.gameTokenId is None:
            await asyncio.sleep(0.1)

        print("✅ Partida encontrada!")

        await send_input(connection, user.gameTokenId)

user = User(input("nickname: "), input("email: "), input("senha: "))

requests.post(f"{httpUrl}/user", json={
    "nickname": user.nickname,
    "email": user.email,
    "password": user.password
})

res = requests.post(f"{httpUrl}/user/login", json={
    "email": user.email,
    "password": user.password
})

data = res.json()
user.addToken(data["data"]["token"])
user.addId(data["data"]["id"])

asyncio.run(connect(user))