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

    static async registerAdmin(admin, authAdmin, c) {
        await http("POST", "/admin", {
            name: `admin-${c}`,
            email: admin.email,
            password: admin.password
        }, 
        authAdmin.token);
    }

    static async adminAuth(admin) {
        const response = await http("POST","/admin/auth",{

            email:admin.email,
            password:admin.password

        });

        admin.setAuth(response.body.data);
    }
}