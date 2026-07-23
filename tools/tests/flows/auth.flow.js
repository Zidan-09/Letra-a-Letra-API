import { http } from "../core/http.js";

export class AuthFlow {
    static async register(user){
        await http("POST","/user",{
            email:user.email,
            password:user.password
        });
    }

    static async login(user){
        const response = await http("POST","/user/auth",{

            email:user.email,
            password:user.password

        });

        user.setAuth(response.body.data);
    }
}