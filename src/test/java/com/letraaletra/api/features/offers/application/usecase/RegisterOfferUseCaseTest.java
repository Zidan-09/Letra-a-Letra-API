package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.offers.application.input.RegisterOfferInput;
import com.letraaletra.api.features.offers.application.input.RegisterOfferRewardInput;
import com.letraaletra.api.features.offers.application.output.RegisterOfferOutput;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterOfferUseCaseTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private CosmeticRepository cosmeticRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private RegisterOfferUseCase useCase;

    private UUID adminId;
    private RegisterOfferInput baseInput;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should successfully register an offer with COIN and GEMS rewards when authorized as admin")
    void shouldRegisterOfferWithStandardRewardsSuccessfully() {
        RegisterOfferRewardInput coinReward = new RegisterOfferRewardInput(RewardType.COIN, null, 500);
        RegisterOfferRewardInput gemsReward = new RegisterOfferRewardInput(RewardType.GEMS, null, 50);

        baseInput = new RegisterOfferInput(
                adminId,
                "Starter Pack",
                CoinType.REAL,
                100,
                List.of(coinReward, gemsReward),
                48
        );

        doNothing().when(adminChecker).check(adminId);

        RegisterOfferOutput output = useCase.execute(baseInput);

        assertNotNull(output);
        assertNotNull(output.offer()); // Assumindo record component ou getter .offer()

        verify(adminChecker, times(1)).check(adminId);
        verify(offerRepository, times(1)).save(any(Offer.class));
        verifyNoInteractions(cosmeticRepository);
    }

    @Test
    @DisplayName("Should successfully register an offer with COSMETIC reward type when the cosmetic reference exists")
    void shouldRegisterOfferWithCosmeticRewardSuccessfully() {
        UUID cosmeticId = UUID.randomUUID();
        Cosmetic mockCosmetic = mock(Cosmetic.class);
        RegisterOfferRewardInput cosmeticReward = new RegisterOfferRewardInput(RewardType.COSMETIC, cosmeticId, 1);

        baseInput = new RegisterOfferInput(
                adminId,
                "Skin Bundle",
                CoinType.HARD,
                150,
                List.of(cosmeticReward),
                24
        );

        doNothing().when(adminChecker).check(adminId);
        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(mockCosmetic));

        RegisterOfferOutput output = useCase.execute(baseInput);

        assertNotNull(output);
        verify(cosmeticRepository, times(1)).find(cosmeticId);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("Should successfully register an offer that contains an empty list of rewards")
    void shouldRegisterOfferWithNoRewardsSuccessfully() {
        baseInput = new RegisterOfferInput(
                adminId,
                "Empty Box",
                CoinType.SOFT,
                100,
                Collections.emptyList(),
                12
        );

        doNothing().when(adminChecker).check(adminId);

        RegisterOfferOutput output = useCase.execute(baseInput);

        assertNotNull(output);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("Should propagate exception and halt processing when admin security verification criteria fails")
    void shouldPropagateExceptionWhenAdminCheckFails() {
        baseInput = new RegisterOfferInput(adminId, "Pack", CoinType.HARD, 10, Collections.emptyList(), 1);

        doThrow(new SecurityException("Forbidden access")).when(adminChecker).check(adminId);

        assertThrows(SecurityException.class, () -> useCase.execute(baseInput));

        verifyNoInteractions(offerRepository);
        verifyNoInteractions(cosmeticRepository);
    }

    @Test
    @DisplayName("Should throw CosmeticNotFoundException when rewards contain a COSMETIC type but reference cannot be resolved")
    void shouldThrowCosmeticNotFoundExceptionWhenCosmeticDoesNotExist() {
        UUID nonExistentCosmeticId = UUID.randomUUID();
        RegisterOfferRewardInput cosmeticReward = new RegisterOfferRewardInput(RewardType.COSMETIC, nonExistentCosmeticId, 1);

        baseInput = new RegisterOfferInput(
                adminId,
                "Premium Bundle",
                CoinType.REAL,
                300,
                List.of(cosmeticReward),
                72
        );

        doNothing().when(adminChecker).check(adminId);
        when(cosmeticRepository.find(nonExistentCosmeticId)).thenReturn(Optional.empty());

        assertThrows(CosmeticNotFoundException.class, () -> useCase.execute(baseInput));

        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when the root register input reference context is null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(adminChecker);
        verifyNoInteractions(offerRepository);
        verifyNoInteractions(cosmeticRepository);
    }
}