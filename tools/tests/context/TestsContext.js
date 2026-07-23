import { AuthFlow } from "../flows/auth.flow.js";
import { User } from "../models/User.js";
import { connect } from "../core/websocket.js";

export class TestContext {
    users = [];

    events = [];

    sockets = [];

    addUser(nickname) {
        const user = new User(nickname, `${nickname.toLowerCase()}@email.com`, "12345678");
        
        this.users.push(user);
    }

    async authUsers() {
        for (const user of this.users) {
            await AuthFlow.register(user);
            await AuthFlow.login(user);
        }
    }

    async connectSockets() {
        this.sockets = await Promise.all(
            this.users.map(user =>
                connect(user, this.events)
            )
        );
    }

    dispose() {
        for (const socket of this.sockets) {
            socket.close();
        }
    }
}