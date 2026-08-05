import {http} from "../core/http.js"

function ensureStatus(response, expected, operation) {
    const expectedStatus = Array.isArray(expected)
        ? expected
        : [expected];

    if (!expectedStatus.includes(response.status)) {
        throw new Error(
            `${operation}: expected ${expectedStatus.join(" or ")}, received ${response.status} =-=-= ${JSON.stringify(response.body)}`
        );
    }
}

export async function runFlow(context) {
    const [user] = context.users;

    let res;

    res = await http(
        "POST",
        "/user",
        { email: user.email, password: "12341234" }
    );

    ensureStatus(res, 201, "Register User");

    res = await http(
        "POST",
        "/user/auth",
        { email: user.email, password: "abacate123" }
    );

    ensureStatus(res, 400, "Send a wrong password");

    res = await http(
        "POST",
        "/user/auth/forgot-password",
        { email: user.email }
    );

    ensureStatus(res, 204, "Send Password Reset Request");

    res = await http(
        "POST",
        "/user/auth/verify-reset-code",
        { email: user.email, code: "123458" }
    );

    ensureStatus(res, 400, "Check if invalid code throws");

    res = await http(
        "POST",
        "/user/auth/verify-reset-code",
        { email: user.email, code: "123456" }
    );

    ensureStatus(res, 204, "Check if valid code pass");

    res = await http(
        "POST",
        "/user/auth/reset-password",
        { email: user.email, newPassword: "abacate123", code: "123456" }
    );

    ensureStatus(res, 204, "Check if the new password was saved");

    res = await http(
        "POST",
        "/user/auth",
        { email: user.email, password: "abacate123" }
    );

    ensureStatus(res, 200, "Authenticate User with the new password");
}