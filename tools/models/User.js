export class User {
    constructor(nickname, email, password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    setAuth(data) {
        this.id = data.id;
        this.token = data.token;
    }
}