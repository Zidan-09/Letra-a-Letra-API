import { AuthFlow } from "../flows/auth.flow.js";
import { Admin } from "../models/Admin.js";

export class AdminTestContext {
    admins = [];

    async authAdmins(adminsQuantity) {
        let admin;

        for (let i = 0; i < adminsQuantity; i++) {
            if (i === 0) {
                admin = new Admin("admin@localhost.com", "lalAdmin2026");

                this.admins.push(admin);

                await AuthFlow.adminAuth(admin);

                continue;
            }

            const newAdmin = new Admin(`admin${i}@email.com`, "12345678");

            this.admins.push(newAdmin);

            await AuthFlow.registerAdmin(newAdmin, admin, i)
        }

        for (const admin of this.admins) {
            await AuthFlow.adminAuth(admin);
        }
    }
}