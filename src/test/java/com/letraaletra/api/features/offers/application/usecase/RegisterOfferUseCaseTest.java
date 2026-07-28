package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.input.RegisterOfferRewardInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferExpirationException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterOfferUseCaseTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private CosmeticRepository cosmeticRepository;

    @Mock
    private AdminChecker adminChecker;

    @Mock
    private AuthenticatedUser principal;

    @InjectMocks
    private RegisterOfferUseCase useCase;

    @Captor
    private ArgumentCaptor<Offer> offerCaptor;

    private static final String DEFAULT_TITLE = "Oferta Especial de Boas-Vindas";
    private static final CoinType DEFAULT_COIN_TYPE = CoinType.REAL;
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(1990);

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(adminChecker).check(any());
    }

    @Nested
    @DisplayName("Validação de Permissões de Acesso (AdminChecker)")
    class AuthorizationTests {

        @Test
        @DisplayName("Deve verificar se o usuário é administrador antes de processar qualquer lógica")
        void shouldCheckAdminPermissionsFirst() {
            RegisterOfferInput input = createValidInputWithoutExpiration(Collections.emptyList());

            useCase.execute(input);

            verify(adminChecker).check(principal);
        }

        @Test
        @DisplayName("Deve lançar exceção e não persistir a oferta quando o usuário não for administrador")
        void shouldThrowExceptionAndNotSaveWhenUserIsNotAdmin() {
            willThrow(new SecurityException("Acesso negado: Requer privilégios de administrador"))
                    .given(adminChecker).check(principal);

            RegisterOfferInput input = createValidInputWithoutExpiration(Collections.emptyList());

            assertThatThrownBy(() -> useCase.execute(input))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Acesso negado");

            verifyNoInteractions(offerRepository);
            verifyNoInteractions(cosmeticRepository);
        }

        @Test
        @DisplayName("Deve repassar a verificação mesmo quando o AuthenticatedUser informado for nulo")
        void shouldThrowExceptionWhenPrincipalIsNull() {
            willThrow(new IllegalArgumentException("Principal não pode ser nulo"))
                    .given(adminChecker).check(null);

            RegisterOfferInput input = new RegisterOfferInput(
                    null,
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    DEFAULT_PRICE,
                    Collections.emptyList(),
                    true,
                    false,
                    0
            );

            assertThatThrownBy(() -> useCase.execute(input))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(offerRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Validações de Expiração de Oferta")
    class ExpirationValidationTests {

        @ParameterizedTest
        @ValueSource(longs = {0, -1, -60, Long.MIN_VALUE})
        @DisplayName("Deve lançar InvalidOfferExpirationException quando a oferta expira e o tempo em minutos for menor ou igual a zero")
        void shouldThrowExceptionWhenExpiresInIsInvalid(long invalidMinutes) {
            RegisterOfferInput input = new RegisterOfferInput(
                    principal,
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    DEFAULT_PRICE,
                    Collections.emptyList(),
                    true,
                    true,
                    invalidMinutes
            );

            assertThatThrownBy(() -> useCase.execute(input))
                    .isInstanceOf(InvalidOfferExpirationException.class);

            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve calcular corretamente a data de expiração no futuro quando expiresIn for maior que zero")
        void shouldCalculateFutureExpirationDateCorrectly() {
            long minutesToExpire = 120;
            LocalDateTime beforeExecution = LocalDateTime.now();

            RegisterOfferInput input = new RegisterOfferInput(
                    principal,
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    DEFAULT_PRICE,
                    Collections.emptyList(),
                    true,
                    true,
                    minutesToExpire
            );

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            assertThat(savedOffer.isHasExpiration()).isTrue();
            assertThat(savedOffer.getExpiresAt()).isNotNull();

            LocalDateTime expectedMinExpiration = beforeExecution.plusMinutes(minutesToExpire);
            LocalDateTime afterExecution = LocalDateTime.now().plusMinutes(minutesToExpire);

            assertThat(savedOffer.getExpiresAt()).isAfterOrEqualTo(expectedMinExpiration);
            assertThat(savedOffer.getExpiresAt()).isBeforeOrEqualTo(afterExecution);
        }

        @Test
        @DisplayName("Deve definir a data de expiração como nula quando a oferta não possui expiração, ignorando o campo expiresIn")
        void shouldSetExpirationDateToNullWhenHasExpirationIsFalse() {
            long ignoredMinutes = -999;
            RegisterOfferInput input = new RegisterOfferInput(
                    principal,
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    DEFAULT_PRICE,
                    Collections.emptyList(),
                    true,
                    false,
                    ignoredMinutes
            );

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            assertThat(savedOffer.isHasExpiration()).isFalse();
            assertThat(savedOffer.getExpiresAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Processamento e Mapeamento de Recompensas (OfferReward)")
    class RewardProcessingTests {

        @Test
        @DisplayName("Deve mapear corretamente recompensa do tipo COIN para SoftCoinsReward")
        void shouldMapCoinRewardCorrectly() {
            int quantity = 500;
            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.COIN, null, quantity);
            RegisterOfferInput input = createValidInputWithoutExpiration(List.of(rewardInput));

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            assertThat(savedOffer.getRewards()).hasSize(1);
            OfferReward reward = savedOffer.getRewards().getFirst();
            assertThat(reward.offerRewardId()).isNotNull();
            assertThat(reward.reward()).isInstanceOf(SoftCoinsReward.class);

            SoftCoinsReward softCoinsReward = (SoftCoinsReward) reward.reward();
            assertThat(softCoinsReward.amount()).isEqualTo(quantity);
            verifyNoInteractions(cosmeticRepository);
        }

        @Test
        @DisplayName("Deve mapear corretamente recompensa do tipo GEMS para HardGemsReward")
        void shouldMapGemsRewardCorrectly() {
            int quantity = 50;
            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.GEMS, null, quantity);
            RegisterOfferInput input = createValidInputWithoutExpiration(List.of(rewardInput));

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            assertThat(savedOffer.getRewards()).hasSize(1);
            OfferReward reward = savedOffer.getRewards().getFirst();
            assertThat(reward.offerRewardId()).isNotNull();
            assertThat(reward.reward()).isInstanceOf(HardGemsReward.class);

            HardGemsReward hardGemsReward = (HardGemsReward) reward.reward();
            assertThat(hardGemsReward.amount()).isEqualTo(quantity);
            verifyNoInteractions(cosmeticRepository);
        }

        @Test
        @DisplayName("Deve mapear corretamente recompensa do tipo COSMETIC ao encontrar o cosmético no repositório")
        void shouldMapCosmeticRewardWhenCosmeticExists() {
            UUID cosmeticId = UUID.randomUUID();
            Cosmetic mockCosmetic = mock(Cosmetic.class);
            given(cosmeticRepository.find(cosmeticId)).willReturn(Optional.of(mockCosmetic));

            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.COSMETIC, cosmeticId, 1);
            RegisterOfferInput input = createValidInputWithoutExpiration(List.of(rewardInput));

            useCase.execute(input);

            verify(cosmeticRepository).find(cosmeticId);
            verify(offerRepository).save(offerCaptor.capture());

            Offer savedOffer = offerCaptor.getValue();
            assertThat(savedOffer.getRewards()).hasSize(1);
            OfferReward reward = savedOffer.getRewards().getFirst();
            assertThat(reward.offerRewardId()).isNotNull();
            assertThat(reward.reward()).isInstanceOf(CosmeticReward.class);

            CosmeticReward cosmeticReward = (CosmeticReward) reward.reward();
            assertThat(cosmeticReward.cosmetic()).isEqualTo(mockCosmetic);
        }

        @Test
        @DisplayName("Deve lançar CosmeticNotFoundException e abortar criação quando o cosmético não for encontrado")
        void shouldThrowCosmeticNotFoundExceptionWhenCosmeticDoesNotExist() {
            UUID unknownCosmeticId = UUID.randomUUID();
            given(cosmeticRepository.find(unknownCosmeticId)).willReturn(Optional.empty());

            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.COSMETIC, unknownCosmeticId, 1);
            RegisterOfferInput input = createValidInputWithoutExpiration(List.of(rewardInput));

            assertThatThrownBy(() -> useCase.execute(input))
                    .isInstanceOf(CosmeticNotFoundException.class);

            verify(cosmeticRepository).find(unknownCosmeticId);
            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve processar múltiplos tipos de recompensas mistas mantendo a ordem e IDs únicos para cada item")
        void shouldProcessMultipleMixedRewardsCorrectly() {
            UUID cosmeticId = UUID.randomUUID();
            Cosmetic mockCosmetic = mock(Cosmetic.class);
            given(cosmeticRepository.find(cosmeticId)).willReturn(Optional.of(mockCosmetic));

            List<RegisterOfferRewardInput> rewardInputs = List.of(
                    new RegisterOfferRewardInput(RewardType.COIN, null, 1000),
                    new RegisterOfferRewardInput(RewardType.GEMS, null, 100),
                    new RegisterOfferRewardInput(RewardType.COSMETIC, cosmeticId, 1)
            );

            RegisterOfferInput input = createValidInputWithoutExpiration(rewardInputs);

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            List<OfferReward> rewards = savedOffer.getRewards();
            assertThat(rewards).hasSize(3);

            assertThat(rewards.get(0).reward()).isInstanceOf(SoftCoinsReward.class);
            assertThat(rewards.get(1).reward()).isInstanceOf(HardGemsReward.class);
            assertThat(rewards.get(2).reward()).isInstanceOf(CosmeticReward.class);

            List<UUID> rewardIds = rewards.stream().map(OfferReward::offerRewardId).toList();
            assertThat(rewardIds).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve permitir o registro de uma oferta sem recompensas (lista vazia)")
        void shouldAllowRegisteringOfferWithEmptyRewards() {
            RegisterOfferInput input = createValidInputWithoutExpiration(Collections.emptyList());

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            assertThat(savedOffer.getRewards()).isEmpty();
            verifyNoInteractions(cosmeticRepository);
        }
    }

    @Nested
    @DisplayName("Criação, Persistência e Saída do Caso de Uso")
    class OfferCreationAndOutputTests {

        @Test
        @DisplayName("Deve construir a oferta com estado desativado (false) por padrão ao criar")
        void shouldCreateOfferAsActiveByDefault() {
            RegisterOfferInput input = createValidInputWithoutExpiration(Collections.emptyList());

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            assertThat(savedOffer.isActive()).isFalse();
        }

        @Test
        @DisplayName("Deve construir a oferta com os dados de título, tipo de moeda e preço numérico corretos")
        void shouldCreateOfferWithCorrectBasicDetails() {
            RegisterOfferInput input = createValidInputWithoutExpiration(Collections.emptyList());

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            assertThat(savedOffer.getTitle()).isEqualTo(DEFAULT_TITLE);
            assertThat(savedOffer.getCoinType()).isEqualTo(DEFAULT_COIN_TYPE);
            assertThat(savedOffer.getPrice()).isEqualTo(DEFAULT_PRICE);
        }

        @Test
        @DisplayName("Deve retornar RegisterOfferOutput contendo a instância exata da oferta salva")
        void shouldReturnRegisterOfferOutputWithSavedOffer() {
            RegisterOfferInput input = createValidInputWithoutExpiration(Collections.emptyList());

            RegisterOfferOutput output = useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            Offer savedOffer = offerCaptor.getValue();

            assertThat(output).isNotNull();
            assertThat(output.offer()).isEqualTo(savedOffer);
        }
    }

    @Nested
    @DisplayName("Casos de Bordas, Resiliência e Tratamento de Entradas Ausentes (*Edge Cases*)")
    class MissingBehaviorEdgeCasesTests {

        @Test
        @DisplayName("[COMPORTAMENTO AUSENTE/BUG] Deve lançar exceção ao passar lista de recompensas nula")
        void shouldHandleNullRewardsListGracefully() {
            RegisterOfferInput input = new RegisterOfferInput(
                    principal,
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    DEFAULT_PRICE,
                    null,
                    true,
                    false,
                    0
            );

            assertThatThrownBy(() -> useCase.execute(input))
                    .isInstanceOf(NullPointerException.class);

            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("[COMPORTAMENTO AUSENTE/BUG] Deve falhar ao receber referência nula para recompensa COSMETIC")
        void shouldFailWhenCosmeticReferenceIsNull() {
            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.COSMETIC, null, 1);
            RegisterOfferInput input = createValidInputWithoutExpiration(List.of(rewardInput));

            assertThatThrownBy(() -> useCase.execute(input))
                    .isInstanceOf(Exception.class);

            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("[COMPORTAMENTO AUSENTE/BUG] Deve verificar comportamento de ofertas com preço negativo")
        void shouldValidateNegativePrice() {
            BigDecimal negativePrice = BigDecimal.valueOf(-500);
            RegisterOfferInput input = new RegisterOfferInput(
                    principal,
                    DEFAULT_TITLE,
                    DEFAULT_COIN_TYPE,
                    negativePrice,
                    Collections.emptyList(),
                    true,
                    false,
                    0
            );

            useCase.execute(input);

            verify(offerRepository).save(offerCaptor.capture());
            assertThat(offerCaptor.getValue().getPrice()).isLessThan(BigDecimal.valueOf(0));
        }
    }

    private RegisterOfferInput createValidInputWithoutExpiration(List<RegisterOfferRewardInput> rewards) {
        return new RegisterOfferInput(
                principal,
                DEFAULT_TITLE,
                DEFAULT_COIN_TYPE,
                DEFAULT_PRICE,
                rewards,
                true,
                false,
                0
        );
    }
}