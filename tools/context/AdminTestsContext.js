import { AuthFlow } from "../flows/auth.flow.js";
import { Admin } from "../models/Admin.js";
import { http } from "../core/http.js";
import { waitForAdminActivationToken } from "../core/mailhog.js";

export class AdminTestContext {
    admins = [];

    async authAdmins(adminsQuantity) {

        const superAdmin = new Admin("admin@localhost.com", "lalAdmin2026");

        await AuthFlow.adminAuth(superAdmin);

        const stamp = Date.now();

        for (let i = 0; i < adminsQuantity; i++) {

            const newAdmin = new Admin(`admin${i}-${stamp}@email.com`, "12345678");

            this.admins.push(newAdmin);

            await AuthFlow.registerAdmin(newAdmin, superAdmin, `${i}-${stamp}`);

            const token = await waitForAdminActivationToken(newAdmin.email);

            await AuthFlow.activateAdmin(newAdmin, token);
        }

        for (const admin of this.admins) {
            await AuthFlow.adminAuth(admin);

            await this.setPermissions(admin, superAdmin);
        }
    }

    async setPermissions(admin, superAdmin) {
        if (admin.id === superAdmin.id) return;

        const actions = ["VIEW", "CREATE", "EDIT", "DELETE", "TOGGLE"];

        const response = await http("PUT", "/admin/" + admin.id, {
                name: admin.name,
                email: admin.email,
                isSuper: true,
                permissions: [
                    { key: "USER", actions: actions },
                    { key: "LOGS", actions: actions },
                    { key: "ADMIN", actions: actions },
                    { key: "COSMETIC", actions: actions },
                    { key: "GAME", actions: actions },
                    { key: "LEVELS", actions: actions },
                    { key: "OFFERS", actions: actions },
                    { key: "TRANSACTIONS", actions: actions },
                    { key: "AUDIT", actions: actions },
                    { key: "TICKET", actions: actions },
                ]
            },
            superAdmin.token);

        if (response.status !== 200) {
            throw new Error(
                `setPermissions: expected 200, received ${response.status} - body=${JSON.stringify(response.body)}`
            );
        }

        return response;
    }
}
