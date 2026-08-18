export class Admin {
    constructor(email, password) {
        this.name = "admin";
        this.email = email;
        this.password = password;
    }

    setAuth(data) {
        this.id = data.id;
        this.token = data.token;
    }
}