import { AuthFlow } from "../flows/auth.flow.js";
import { User } from "../models/User.js";
import { connect } from "../core/websocket.js";

export class TestContext {
    users = [];

    events = new Map();

    sharedEvents = [];
    sharedEventIds = new Set();

    sockets = [];

    addUser(nickname) {
        const user = new User(
            nickname,
            `${nickname.toLowerCase()}@email.com`,
            "12345678"
        );

        this.users.push(user);
        this.events.set(user, []);
    }

    async authUsers() {
        for (const user of this.users) {
            try {
                await AuthFlow.register(user);
            } catch (e) {
                const msg = String(e.message).toLowerCase();
                const isDuplicate = msg.includes("already") || msg.includes("em uso") || msg.includes("duplicate");
                if (!isDuplicate) {
                    throw e;
                }
            }
            await AuthFlow.login(user);
            if (!user.id || !user.token) {
                throw new Error(`authUsers: user ${user.nickname} missing id/token after login`);
            }
        }
    }

    async connectSockets() {
        this.sockets = await Promise.all(
            this.users.map(user =>
                connect(
                    user,
                    this.events.get(user),
                    event => this.addSharedEvent(event)
                )
            )
        );
    }

    addSharedEvent(event) {
        if (this.sharedEventIds.has(event.eventId)) {
            return;
        }

        this.sharedEventIds.add(event.eventId);
        this.sharedEvents.push(event);

        if (this.sharedEvents._listeners) {
            this.sharedEvents._listeners =
                this.sharedEvents._listeners.filter(
                    listener => !listener(event)
                );
        }
    }

    getSharedEvents() {
        return this.sharedEvents;
    }

    clearEvents() {
        for (const events of this.events.values()) {
            events.length = 0;
            delete events._listeners;
        }

        this.sharedEvents.length = 0;
        this.sharedEventIds.clear();
        delete this.sharedEvents._listeners;
    }

    dispose() {
        for (const socket of this.sockets) {
            socket.close();
        }
    }
}