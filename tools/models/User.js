export class User {
    constructor(nickname, email, password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    setAuth(data) {
        if (!data || typeof data !== "object") {
            throw new Error(`Auth data missing or invalid: ${JSON.stringify(data)}`);
        }

        const id = data.id ?? data.userId ?? data.user_id ?? data.userID;
        const token = data.token ?? data.accessToken ?? data.access_token ?? data.jwt;

        if (!id || !token) {
            throw new Error(`Auth response missing id/token: ${JSON.stringify(data)}`);
        }

        this.id = id;
        this.token = token;
    }
}