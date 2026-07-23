import { http } from "../core/http.js";
import { waitForEvent } from "../core/waitForEvent.js";

function ensureStatus(response, expected, operation) {
    const expectedStatus = Array.isArray(expected) ? expected : [expected];

    if (!expectedStatus.includes(response.status)) {
        throw new Error(
            `${operation}: expected ${expectedStatus.join(" or ")}, recived ${response.status}`
        );
    }
}

export async function runFlow(context) {
    const [zidan, pombao, wadawueu, torugo] = context.users;
    let res;

    // Fluxo 1: Solicitação de amizade

    res = await http(
        "POST",
        "/friend/request",
        {
            friendId: pombao.id
        },
        zidan.token
    );

    ensureStatus(res, 200, "Send friend request");

    await waitForEvent(
        e => e.event === "RECEIVE_FRIEND_REQUEST",
        context.events
    );

    res = await http(
        "GET",
        "/friend/pending",
        undefined,
        pombao.token
    );

    ensureStatus(res, 200, "Get pending friend requests");

    // Fluxo 2: Solicitação duplicada e aceite

    res = await http(
        "POST",
        "/friend/request",
        {
            friendId: pombao.id
        },
        zidan.token
    );

    ensureStatus(res, 400, "Duplicate friend request");

    res = await http(
        "PATCH",
        "/friend/accept",
        {
            friendId: pombao.id
        },
        zidan.token
    );

    ensureStatus(res, 400, "Sender accepting own request");

    res = await http(
        "PATCH",
        "/friend/accept",
        {
            friendId: zidan.id
        },
        pombao.token
    );

    ensureStatus(res, [200, 204], "Receiver accepting friend request");

    res = await http(
        "GET",
        "/friend/pending",
        undefined,
        pombao.token
    );

    ensureStatus(res, 200, "Get pending friend requests");

    res = await http(
        "GET",
        "/friend",
        undefined,
        zidan.token
    );

    ensureStatus(res, 200, "Get friends list (Zidan)");

    res = await http(
        "GET",
        "/friend",
        undefined,
        pombao.token
    );

    ensureStatus(res, 200, "Get friends list (Pombao)");

    // Fluxo 3: Nova solicitação para amigo

    res = await http(
        "POST",
        "/friend/request",
        {
            friendId: pombao.id
        },
        zidan.token
    );

    ensureStatus(res, 400, "Request already accepted friend");

     // Fluxo 4: Remoção da amizade

    res = await http(
        "PATCH",
        "/friend/remove",
        {
            friendId: pombao.id
        },
        zidan.token
    );

    ensureStatus(res, 200, "Remover amizade");

    res = await http(
        "GET",
        "/friend",
        undefined,
        zidan.token
    );

    ensureStatus(res, 200, "Buscar amigos do Zidan");

    res = await http(
        "GET",
        "/friend",
        undefined,
        pombao.token
    );

    ensureStatus(res, 200, "Buscar amigos do Pombao");

    res = await http(
        "PATCH",
        "/friend/remove",
        {
            friendId: pombao.id
        },
        zidan.token
    );

    ensureStatus(res, 400, "Remover amizade inexistente");

    // Fluxo 5: Recusar solicitação

    res = await http(
        "POST",
        "/friend/request",
        {
            friendId: torugo.id
        },
        wadawueu.token
    );

    ensureStatus(res, 200, "Enviar solicitação");

    await waitForEvent(
        e => e.event === "RECEIVE_FRIEND_REQUEST",
        context.events
    );

    res = await http(
        "GET",
        "/friend/pending",
        undefined,
        torugo.token
    );

    ensureStatus(res, 200, "Buscar pendentes");

    res = await http(
        "PATCH",
        "/friend/reject",
        {
            friendId: torugo.id
        },
        wadawueu.token
    );

    ensureStatus(res, 400, "Remetente recusando própria solicitação");

    res = await http(
        "PATCH",
        "/friend/reject",
        {
            friendId: wadawueu.id
        },
        torugo.token
    );

    ensureStatus(res, 200, "Destinatário recusando solicitação");

    res = await http(
        "GET",
        "/friend/pending",
        undefined,
        torugo.token
    );

    ensureStatus(res, 200, "Buscar pendentes após recusa");

    res = await http(
        "GET",
        "/friend",
        undefined,
        wadawueu.token
    );

    ensureStatus(res, 200, "Buscar amigos Wadawueu");

    res = await http(
        "GET",
        "/friend",
        undefined,
        torugo.token
    );

    ensureStatus(res, 200, "Buscar amigos Torugo");

    // Fluxo 6: Reenviar solicitação

    res = await http(
        "POST",
        "/friend/request",
        {
            friendId: torugo.id
        },
        wadawueu.token
    );

    ensureStatus(res, 200, "Reenviar solicitação");

    await waitForEvent(
        e => e.event === "RECEIVE_FRIEND_REQUEST",
        context.events
    );

    res = await http(
        "GET",
        "/friend/pending",
        undefined,
        torugo.token
    );

    ensureStatus(res, 200, "Buscar pendentes após reenvio");
}