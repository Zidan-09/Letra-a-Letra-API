package com.letraaletra.api.features.offers.domain;

import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class OfferTest {

    private static final String DEFAULT_TITLE = "Oferta de Pacote de Moedas";
    private static final CoinType DEFAULT_COIN_TYPE = CoinType.REAL;
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(2990);

    @Nested
    @DisplayName("Criação de Oferta (Método Factory 'create' e Construtor Direct)")
    class CreationTests {

        @Test
        @DisplayName("Deve instanciar uma oferta via fábrica estática 'create' gerando UUID e data de criação válidos")
        void shouldCreateOfferViaStaticFactory() {
            LocalDateTime beforeCreation = LocalDateTime.now();
            long minutesToExpire = 7;
            OfferReward mockReward = mock(OfferReward.class);
            List<OfferReward> rewards = List.of(mockReward);

            Offer offer = Offer.create(
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    DEFAULT_PRICE,
                    rewards,
                    true,
                    true,
                    minutesToExpire
            );

            LocalDateTime afterCreation = LocalDateTime.now();

            assertThat(offer.getOfferId()).isNotNull();
            assertThat(offer.getTitle()).isEqualTo(DEFAULT_TITLE);
            assertThat(offer.getCoinType()).isEqualTo(DEFAULT_COIN_TYPE);
            assertThat(offer.getPrice()).isEqualTo(DEFAULT_PRICE);
            assertThat(offer.getRewards()).containsExactly(mockReward);
            assertThat(offer.isActive()).isFalse();
            assertThat(offer.isHasExpiration()).isTrue();
            assertThat(offer.getCreatedAt()).isAfterOrEqualTo(beforeCreation).isBeforeOrEqualTo(afterExecutionDate(afterCreation));
        }

        @Test
        @DisplayName("Deve instanciar uma oferta através do construtor completo com todos os valores explicitamente fornecidos")
        void shouldInstantiateOfferViaFullConstructor() {
            UUID customId = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(12);

            Offer offer = new Offer(
                    customId,
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    DEFAULT_PRICE,
                    Collections.emptyList(),
                    false,
                    true,
                    true,
                    expiresAt,
                    createdAt
            );

            assertThat(offer.getOfferId()).isEqualTo(customId);
            assertThat(offer.getTitle()).isEqualTo(DEFAULT_TITLE);
            assertThat(offer.getCoinType()).isEqualTo(DEFAULT_COIN_TYPE);
            assertThat(offer.getPrice()).isEqualTo(DEFAULT_PRICE);
            assertThat(offer.getRewards()).isEmpty();
            assertThat(offer.isActive()).isFalse();
            assertThat(offer.isHasExpiration()).isTrue();
            assertThat(offer.getExpiresAt()).isEqualTo(expiresAt);
            assertThat(offer.getCreatedAt()).isEqualTo(createdAt);
        }

        private LocalDateTime afterExecutionDate(LocalDateTime time) {
            return time.plusSeconds(1);
        }
    }

    @Nested
    @DisplayName("Transição de Estado de Ativação/Desativação (disable e enable)")
    class StateTransitionTests {

        @Test
        @DisplayName("Deve lançar InvalidOfferStatusException ao tentar desativar uma oferta já inativa")
        void shouldThrowExceptionWhenDisablingAlreadyInactiveOffer() {
            Offer offer = createOffer();

            assertThatThrownBy(offer::disable)
                    .isInstanceOf(InvalidOfferStatusException.class);

            assertThat(offer.isActive()).isFalse();
        }

        @Test
        @DisplayName("Deve ativar uma oferta inativa com sucesso")
        void shouldEnableInactiveOfferSuccessfully() {
            Offer offer = createOffer();

            offer.enable();

            assertThat(offer.isActive()).isTrue();
        }

        @Test
        @DisplayName("Deve lançar InvalidOfferStatusException ao tentar ativar uma oferta que já está ativa")
        void shouldThrowExceptionWhenEnablingAlreadyActiveOffer() {
            Offer offer = createOffer();
            offer.enable();

            assertThatThrownBy(offer::enable)
                    .isInstanceOf(InvalidOfferStatusException.class);

            assertThat(offer.isActive()).isTrue();
        }

        @Test
        @DisplayName("Deve desativar uma oferta ativa com sucesso")
        void shouldDisableActiveOfferSuccessfully() {
            Offer offer = createOffer();
            offer.enable();

            offer.disable();

            assertThat(offer.isActive()).isFalse();
        }

        @Test
        @DisplayName("Deve permitir ciclos alternados válidos de habilitação e desabilitação")
        void shouldAllowAlternatingStateTransitions() {
            Offer offer = createOffer();

            offer.enable();
            assertThat(offer.isActive()).isTrue();

            offer.disable();
            assertThat(offer.isActive()).isFalse();

            offer.enable();
            assertThat(offer.isActive()).isTrue();

            offer.disable();
            assertThat(offer.isActive()).isFalse();
        }

        private Offer createOffer() {
            return Offer.create(
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    DEFAULT_PRICE,
                    Collections.emptyList(),
                    false,
                    false,
                    0
            );
        }
    }
}