package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.shared.application.port.AdminChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
class CreateLevelUseCaseTest {

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private CosmeticRepository cosmeticRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private CreateLevelUseCase useCase;

    @Captor
    private ArgumentCaptor<Level> levelCaptor;

    private UUID adminId;
    private int targetLevel;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        targetLevel = 10;
    }

    @Test
    @DisplayName("Should successfully create a level with COIN reward type when authorized as admin")
    void shouldCreateLevelWithCoinRewardSuccessfully() {
        CreateLevelRewardInput coinRewardInput = new CreateLevelRewardInput(RewardType.COIN, null, 500);
        CreateLevelInput input = new CreateLevelInput(adminId, targetLevel, List.of(coinRewardInput));

        doNothing().when(adminChecker).check(adminId);

        CreateLevelOutput output = useCase.execute(input);

        assertNull(output);
        verify(adminChecker, times(1)).check(adminId);
        verify(levelRepository, times(1)).save(levelCaptor.capture());
        verifyNoInteractions(cosmeticRepository);

        Level savedLevel = levelCaptor.getValue();
        assertNotNull(savedLevel);
    }

    @Test
    @DisplayName("Should successfully create a level with GEMS reward type when authorized as admin")
    void shouldCreateLevelWithGemsRewardSuccessfully() {
        CreateLevelRewardInput gemsRewardInput = new CreateLevelRewardInput(RewardType.GEMS, null, 50);
        CreateLevelInput input = new CreateLevelInput(adminId, targetLevel, List.of(gemsRewardInput));

        doNothing().when(adminChecker).check(adminId);

        useCase.execute(input);

        verify(levelRepository, times(1)).save(any(Level.class));
        verifyNoInteractions(cosmeticRepository);
    }

    @Test
    @DisplayName("Should successfully create a level with COSMETIC reward type when the reference exists")
    void shouldCreateLevelWithCosmeticRewardSuccessfully() {
        UUID cosmeticId = UUID.randomUUID();
        Cosmetic mockCosmetic = mock(Cosmetic.class);
        CreateLevelRewardInput cosmeticRewardInput = new CreateLevelRewardInput(RewardType.COSMETIC, cosmeticId, 1);
        CreateLevelInput input = new CreateLevelInput(adminId, targetLevel, List.of(cosmeticRewardInput));

        doNothing().when(adminChecker).check(adminId);
        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(mockCosmetic));

        useCase.execute(input);

        verify(cosmeticRepository, times(1)).find(cosmeticId);
        verify(levelRepository, times(1)).save(any(Level.class));
    }

    @Test
    @DisplayName("Should successfully create a level with multiple mixed reward types")
    void shouldCreateLevelWithMixedRewardsSuccessfully() {
        UUID cosmeticId = UUID.randomUUID();
        Cosmetic mockCosmetic = mock(Cosmetic.class);
        CreateLevelRewardInput coinReward = new CreateLevelRewardInput(RewardType.COIN, null, 100);
        CreateLevelRewardInput gemsReward = new CreateLevelRewardInput(RewardType.GEMS, null, 10);
        CreateLevelRewardInput cosmeticReward = new CreateLevelRewardInput(RewardType.COSMETIC, cosmeticId, 1);

        CreateLevelInput input = new CreateLevelInput(adminId, targetLevel, List.of(coinReward, gemsReward, cosmeticReward));

        doNothing().when(adminChecker).check(adminId);
        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(mockCosmetic));

        useCase.execute(input);

        verify(levelRepository, times(1)).save(any(Level.class));
        verify(cosmeticRepository, times(1)).find(cosmeticId);
    }

    @Test
    @DisplayName("Should successfully create a level with an empty list of rewards")
    void shouldCreateLevelWithNoRewardsSuccessfully() {
        CreateLevelInput input = new CreateLevelInput(adminId, targetLevel, Collections.emptyList());

        doNothing().when(adminChecker).check(adminId);

        useCase.execute(input);

        verify(levelRepository, times(1)).save(any(Level.class));
    }

    @Test
    @DisplayName("Should propagate exception when admin security verification criteria fails")
    void shouldPropagateExceptionWhenAdminCheckFails() {
        CreateLevelInput input = new CreateLevelInput(adminId, targetLevel, Collections.emptyList());

        doThrow(new SecurityException("Unauthorized access")).when(adminChecker).check(adminId);

        assertThrows(SecurityException.class, () -> useCase.execute(input));

        verifyNoInteractions(levelRepository);
        verifyNoInteractions(cosmeticRepository);
    }

    @Test
    @DisplayName("Should throw CosmeticNotFoundException when reward type is COSMETIC but reference cannot be resolved")
    void shouldThrowExceptionWhenCosmeticNotFound() {
        UUID nonExistentCosmeticId = UUID.randomUUID();
        CreateLevelRewardInput cosmeticRewardInput = new CreateLevelRewardInput(RewardType.COSMETIC, nonExistentCosmeticId, 1);
        CreateLevelInput input = new CreateLevelInput(adminId, targetLevel, List.of(cosmeticRewardInput));

        doNothing().when(adminChecker).check(adminId);
        when(cosmeticRepository.find(nonExistentCosmeticId)).thenReturn(Optional.empty());

        assertThrows(CosmeticNotFoundException.class, () -> useCase.execute(input));

        verifyNoInteractions(levelRepository);
    }

    @Test
    @DisplayName("Should fail or throw exception when root payload context structure parameters are null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(adminChecker);
        verifyNoInteractions(levelRepository);
        verifyNoInteractions(cosmeticRepository);
    }
}