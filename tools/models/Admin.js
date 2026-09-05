export class Admin {
    constructor(email, password) {
        this.name = "admin";
        this.email = email;
        this.password = password;
    }

    setAuth(data) {
        if (!data || typeof data !== "object") {
            throw new Error(`Admin auth data missing: ${JSON.stringify(data)}`);
        }
        const id = data.id ?? data.userId ?? data.adminId ?? data.admin_id;
        const token = data.token ?? data.accessToken ?? data.access_token ?? data.jwt;
        if (!id || !token) {
            throw new Error(`Admin auth response missing id/token: ${JSON.stringify(data)}`);
        }
        this.id = id;
        this.token = token;
    }
}