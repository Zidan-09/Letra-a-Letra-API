import { http } from "../core/http.js";

function ensureStatus(response, expected, operation) {
    const expectedStatus = Array.isArray(expected) ? expected : [expected];

    if (!expectedStatus.includes(response.status)) {
        throw new Error(
            `${operation}: expected ${expectedStatus.join(" or ")}, received ${response.status} - body=${JSON.stringify(response.body)}`
        );
    }
}

export class AuthFlow {
    static async register(user){
        const response = await http("POST","/user",{
            email:user.email,
            password:user.password
        });

        ensureStatus(response, [200, 201], "register");

        return response;
    }

    static async login(user){
        const response = await http("POST","/user/auth",{

            email:user.email,
            password:user.password

        });

        ensureStatus(response, 200, "login");

        const payload = response.body?.data ?? response.body;

        if (!payload) {
            throw new Error(`login: missing data in response body=${JSON.stringify(response.body)}`);
        }

        user.setAuth(payload);

        if (!user.id || !user.token) {
            throw new Error(`login: user id/token not set after auth payload=${JSON.stringify(payload)}`);
        }

        return response;
    }

    static async registerAdmin(admin, authAdmin, c) {
        const response = await http("POST", "/admin", {
            name: `admin-${c}`,
            email: admin.email,
            password: admin.password
        }, 
        authAdmin.token);

        ensureStatus(response, [200, 201], "registerAdmin");

        return response;
    }

    static async adminAuth(admin) {
        const response = await http("POST","/admin/auth",{

            email:admin.email,
            password:admin.password

        });

        ensureStatus(response, 200, "adminAuth");

        const payload = response.body?.data ?? response.body;

        if (!payload) {
            throw new Error(`adminAuth: missing data body=${JSON.stringify(response.body)}`);
        }

        admin.setAuth(payload);

        if (!admin.id || !admin.token) {
            throw new Error(`adminAuth: admin id/token not set payload=${JSON.stringify(payload)}`);
        }

        return response;
    }
}