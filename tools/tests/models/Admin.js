export class Admin {
    constructor(email, password) {
        this.email = email;
        this.password = password;
    }

    setAuth(data) {
        this.id = data.id;
        this.token = data.token;
    }
}