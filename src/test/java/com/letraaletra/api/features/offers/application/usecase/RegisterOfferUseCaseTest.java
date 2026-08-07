package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.input.RegisterOfferRewardInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.RewardFactory;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterOfferUseCaseTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private AdminChecker adminChecker;

    @Mock
    private RewardFactory rewardFactory;

    private RegisterOfferUseCase useCase;

    @Captor
    private ArgumentCaptor<Offer> offerCaptor;

    private AuthenticatedUser principal;
    private final PermissionKey key = PermissionKey.OFFERS;
    private final PermissionAction action = PermissionAction.CREATE;

    @BeforeEach
    void setUp() {
        principal = mock(AuthenticatedUser.class);
        useCase = new RegisterOfferUseCase(offerRepository, adminChecker, rewardFactory);
    }

    @Test
    @DisplayName("Should successfully register a valid offer when authorized as admin")
    void shouldRegisterOfferSuccessfully() {
        RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.COIN, null, 100);
        RegisterOfferInput input = new RegisterOfferInput(
                principal,
                "Pacote de Moedas",
                CoinType.REAL,
                BigDecimal.valueOf(10.0),
                List.of(rewardInput),
                true,
                false,
                0L
        );

        doNothing().when(adminChecker).check(principal, key, action);
        when(rewardFactory.create(RewardType.COIN, 100, null)).thenReturn(new SoftCoinsReward(100));

        RegisterOfferOutput output = useCase.execute(input);

        assertNotNull(output);
        verify(adminChecker, times(1)).check(principal, key, action);
        verify(offerRepository, times(1)).save(offerCaptor.capture());

        Offer savedOffer = offerCaptor.getValue();
        assertNotNull(savedOffer);
        assertEquals("Pacote de Moedas", savedOffer.getTitle());
        assertEquals(BigDecimal.valueOf(10.0), savedOffer.getPrice());
        assertEquals(1, savedOffer.getRewards().size());
    }

    @Test
    @DisplayName("Should propagate exception when admin security verification fails")
    void shouldPropagateExceptionWhenAdminCheckFails() {
        RegisterOfferInput input = new RegisterOfferInput(
                principal,
                "Oferta Inválida",
                CoinType.SOFT,
                BigDecimal.valueOf(5.0),
                Collections.emptyList(),
                false,
                false,
                0L
        );

        doThrow(new SecurityException("Unauthorized access")).when(adminChecker).check(principal, key, action);

        assertThrows(SecurityException.class, () -> useCase.execute(input));

        verifyNoInteractions(offerRepository);
        verifyNoInteractions(rewardFactory);
    }

    @Test
    @DisplayName("Should fail when input parameters are null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(adminChecker);
        verifyNoInteractions(offerRepository);
        verifyNoInteractions(rewardFactory);
    }

    @Nested
    @DisplayName("Processamento e Mapeamento de Recompensas (OfferReward)")
    class RewardProcessingTests {

        @Test
        @DisplayName("shouldMapCoinRewardCorrectly")
        void shouldMapCoinRewardCorrectly() {
            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.COIN, null, 100);
            RegisterOfferInput input = new RegisterOfferInput(
                    principal, "Oferta Moeda", CoinType.REAL, BigDecimal.TEN, List.of(rewardInput), true, false, 0L
            );

            doNothing().when(adminChecker).check(principal, key, action);
            when(rewardFactory.create(eq(RewardType.COIN), eq(100), any()))
                    .thenReturn(new SoftCoinsReward(100));

            RegisterOfferOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(rewardFactory, times(1)).create(eq(RewardType.COIN), eq(100), any());
            verify(offerRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("shouldMapGemsRewardCorrectly")
        void shouldMapGemsRewardCorrectly() {
            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.GEMS, null, 50);
            RegisterOfferInput input = new RegisterOfferInput(
                    principal, "Oferta Gemas", CoinType.REAL, BigDecimal.TEN, List.of(rewardInput), true, false, 0L
            );

            doNothing().when(adminChecker).check(principal, key, action);
            when(rewardFactory.create(eq(RewardType.GEMS), eq(50), any()))
                    .thenReturn(new HardGemsReward(50));

            RegisterOfferOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(rewardFactory, times(1)).create(eq(RewardType.GEMS), eq(50), any());
            verify(offerRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("shouldMapCosmeticRewardWhenCosmeticExists")
        void shouldMapCosmeticRewardWhenCosmeticExists() {
            UUID cosmeticId = UUID.randomUUID();
            Cosmetic mockCosmetic = mock(Cosmetic.class);

            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.COSMETIC, cosmeticId, 1);
            RegisterOfferInput input = new RegisterOfferInput(
                    principal, "Oferta Cosmético", CoinType.REAL, BigDecimal.TEN, List.of(rewardInput), true, false, 0L
            );

            doNothing().when(adminChecker).check(principal, key, action);
            when(rewardFactory.create(eq(RewardType.COSMETIC), eq(1), eq(cosmeticId)))
                    .thenReturn(new CosmeticReward(mockCosmetic));

            RegisterOfferOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(rewardFactory, times(1)).create(eq(RewardType.COSMETIC), eq(1), eq(cosmeticId));
            verify(offerRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("shouldThrowCosmeticNotFoundExceptionWhenCosmeticDoesNotExist")
        void shouldThrowCosmeticNotFoundExceptionWhenCosmeticDoesNotExist() {
            UUID cosmeticId = UUID.randomUUID();
            RegisterOfferRewardInput rewardInput = new RegisterOfferRewardInput(RewardType.COSMETIC, cosmeticId, 1);
            RegisterOfferInput input = new RegisterOfferInput(
                    principal, "Oferta Inválida", CoinType.REAL, BigDecimal.TEN, List.of(rewardInput), true, false, 0L
            );

            doNothing().when(adminChecker).check(principal, key, action);
            when(rewardFactory.create(eq(RewardType.COSMETIC), anyInt(), eq(cosmeticId)))
                    .thenThrow(new CosmeticNotFoundException());

            assertThrows(
                    CosmeticNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(offerRepository, never()).save(any());
        }

        @Test
        @DisplayName("shouldProcessMultipleMixedRewardsCorrectly")
        void shouldProcessMultipleMixedRewardsCorrectly() {
            UUID cosmeticId = UUID.randomUUID();
            RegisterOfferRewardInput coinReward = new RegisterOfferRewardInput(RewardType.COIN, null, 100);
            RegisterOfferRewardInput gemsReward = new RegisterOfferRewardInput(RewardType.GEMS, null, 10);
            RegisterOfferRewardInput cosmeticReward = new RegisterOfferRewardInput(RewardType.COSMETIC, cosmeticId, 1);

            RegisterOfferInput input = new RegisterOfferInput(
                    principal, "Super Combo", CoinType.REAL, BigDecimal.valueOf(50),
                    List.of(coinReward, gemsReward, cosmeticReward), true, false, 0L
            );

            doNothing().when(adminChecker).check(principal, key, action);
            when(rewardFactory.create(any(), anyInt(), any()))
                    .thenReturn(new SoftCoinsReward(100));

            RegisterOfferOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(rewardFactory, times(3)).create(any(), anyInt(), any());
            verify(offerRepository, times(1)).save(any());
        }
    }
}