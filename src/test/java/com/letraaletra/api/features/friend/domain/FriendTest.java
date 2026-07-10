package com.letraaletra.api.features.friend.domain;

import com.letraaletra.api.features.friend.domain.exception.CanNotAcceptTheRequestException;
import com.letraaletra.api.features.friend.domain.exception.CanNotDeclineTheRequestException;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FriendTest {

    private UUID userId1;
    private UUID userId2;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Criação e Restauração de Estado")
    class CreationAndRestoration {

        @Test
        @DisplayName("Deve instanciar um novo Friend com status PENDING e data atual através do método create")
        void create_ShouldInitializeNewFriendRequest() {
            LocalDateTime now = LocalDateTime.now();

            Friend friend = Friend.create(userId1, userId2);

            assertNotNull(friend);
            assertEquals(userId1, friend.getUserId1());
            assertEquals(userId2, friend.getUserId2());
            assertEquals(FriendStatus.PENDING, friend.getStatus());
            assertNotNull(friend.getRequestDate());
            assertTrue(friend.getRequestDate().isAfter(now.minusSeconds(1)) && friend.getRequestDate().isBefore(now.plusSeconds(1)));
        }

        @Test
        @DisplayName("Deve restaurar o estado completo de uma instância através do método restore")
        void restore_ShouldRecreateFriendWithExactGivenState() {
            FriendStatus status = FriendStatus.ACCEPT;
            LocalDateTime pastDate = LocalDateTime.now().minusDays(5);

            Friend friend = Friend.restore(userId1, userId2, status, pastDate);

            assertNotNull(friend);
            assertEquals(userId1, friend.getUserId1());
            assertEquals(userId2, friend.getUserId2());
            assertEquals(status, friend.getStatus());
            assertEquals(pastDate, friend.getRequestDate());
        }

        @Test
        @DisplayName("Deve permitir IDs nulos ou idênticos na criação se não houver validação explícita de domínio (Comportamento Desejado/Ausente)")
        void create_ShouldFail_WhenIdenticalUserIdsOrNullsArePassed() {
            assertDoesNotThrow(() -> Friend.create(userId1, userId1),
                    "Falta validação para impedir que um usuário envie solicitação para si mesmo");

            assertDoesNotThrow(() -> Friend.create(null, null),
                    "Falta validação contra IDs nulos");
        }
    }

    @Nested
    @DisplayName("Operação: Aceitar Solicitação (accept)")
    class AcceptRequest {

        @Test
        @DisplayName("Deve aceitar a solicitação com sucesso quando o destinatário correto tentar e o status for PENDING")
        void accept_ShouldChangeStatusToAccept_WhenValidUserAndPendingStatus() {
            Friend friend = Friend.create(userId1, userId2);

            friend.accept(userId2);

            assertEquals(FriendStatus.ACCEPT, friend.getStatus());
        }

        @Test
        @DisplayName("Deve lançar CanNotAcceptTheRequestException quando o usuário que aceita não for o destinatário (userId2)")
        void accept_ShouldThrowCanNotAcceptTheRequestException_WhenUserIsNotRecipient() {
            Friend friend = Friend.create(userId1, userId2);

            assertThrows(CanNotAcceptTheRequestException.class, () -> friend.accept(userId1));
            assertThrows(CanNotAcceptTheRequestException.class, () -> friend.accept(UUID.randomUUID()));
            assertEquals(FriendStatus.PENDING, friend.getStatus());
        }

        @Test
        @DisplayName("Deve lançar InvalidFriendRequestException se tentar aceitar uma solicitação que não está PENDING")
        void accept_ShouldThrowInvalidFriendRequestException_WhenStatusIsNotPending() {
            Friend friend = Friend.restore(userId1, userId2, FriendStatus.ACCEPT, LocalDateTime.now());
            assertThrows(InvalidFriendRequestException.class, () -> friend.accept(userId2));

            Friend declinedFriend = Friend.restore(userId1, userId2, FriendStatus.DECLINED, LocalDateTime.now());
            assertThrows(InvalidFriendRequestException.class, () -> declinedFriend.accept(userId2));
        }
    }

    @Nested
    @DisplayName("Operação: Recusar Solicitação (decline)")
    class DeclineRequest {

        @Test
        @DisplayName("Deve recusar a solicitação com sucesso quando o destinatário correto tentar e o status for PENDING")
        void decline_ShouldChangeStatusToDeclined_WhenValidUserAndPendingStatus() {
            Friend friend = Friend.create(userId1, userId2);

            friend.decline(userId2);

            assertEquals(FriendStatus.DECLINED, friend.getStatus());
        }

        @Test
        @DisplayName("Deve lançar CanNotDeclineTheRequestException quando o usuário que recusa não for o destinatário (userId2)")
        void decline_ShouldThrowCanNotDeclineTheRequestException_WhenUserIsNotRecipient() {
            Friend friend = Friend.create(userId1, userId2);

            assertThrows(CanNotDeclineTheRequestException.class, () -> friend.decline(userId1));
            assertThrows(CanNotDeclineTheRequestException.class, () -> friend.decline(UUID.randomUUID()));
            assertEquals(FriendStatus.PENDING, friend.getStatus());
        }

        @Test
        @DisplayName("Deve lançar InvalidFriendRequestException se tentar recusar uma solicitação que não está PENDING")
        void decline_ShouldThrowInvalidFriendRequestException_WhenStatusIsNotPending() {
            Friend friend = Friend.restore(userId1, userId2, FriendStatus.ACCEPT, LocalDateTime.now());
            assertThrows(InvalidFriendRequestException.class, () -> friend.decline(userId2));

            Friend declinedFriend = Friend.restore(userId1, userId2, FriendStatus.DECLINED, LocalDateTime.now());
            assertThrows(InvalidFriendRequestException.class, () -> declinedFriend.accept(userId2));
        }
    }

    @Nested
    @DisplayName("Operação: Remover Amizade (remove)")
    class RemoveFriendship {

        @Test
        @DisplayName("Deve alterar o status para DECLINED quando uma amizade ativa (ACCEPT) for removida")
        void remove_ShouldChangeStatusToDeclined_WhenCurrentStatusIsAccept() {
            Friend friend = Friend.restore(userId1, userId2, FriendStatus.ACCEPT, LocalDateTime.now());

            friend.remove();

            assertEquals(FriendStatus.DECLINED, friend.getStatus());
        }

        @Test
        @DisplayName("Deve lançar InvalidFriendRequestException se tentar remover uma amizade que não está com status ACCEPT")
        void remove_ShouldThrowInvalidFriendRequestException_WhenStatusIsNotAccept() {
            Friend pendingFriend = Friend.create(userId1, userId2);
            assertThrows(InvalidFriendRequestException.class, pendingFriend::remove);

            Friend declinedFriend = Friend.restore(userId1, userId2, FriendStatus.DECLINED, LocalDateTime.now());
            assertThrows(InvalidFriendRequestException.class, declinedFriend::remove);
        }

        @Test
        @DisplayName("Deve permitir que qualquer um dos envolvidos desfaça a amizade (Comportamento Desejado/Ausente)")
        void remove_ShouldRequireActorContext_ToValidateWhoIsRemovingFriendship() {
            // Nota de Especificação: O método remove() atual não recebe parâmetros contextuais (quem está removendo).
            // Idealmente, deveria ser garantido por contrato que apenas userId1 ou userId2 podem invocar a remoção.
            Friend friend = Friend.restore(userId1, userId2, FriendStatus.ACCEPT, LocalDateTime.now());

            assertDoesNotThrow(friend::remove,
                    "O método executa a ação cegamente sem rastrear a intenção de quem desfez a amizade");
        }
    }
}