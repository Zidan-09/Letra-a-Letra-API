package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.input.UpdateLevelInput;
import com.letraaletra.api.features.levels.application.output.UpdateLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.offers.domain.RewardType;
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
class UpdateLevelUseCaseTest {

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private CosmeticRepository cosmeticRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private UpdateLevelUseCase useCase;

    private UUID adminId;
    private UUID levelId;
    private int newTargetLevel;
    private Level mockLevel;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        levelId = UUID.randomUUID();
        newTargetLevel = 15;
        mockLevel = mock(Level.class);
    }

    @Test
    @DisplayName("Should successfully update a level with COIN and GEMS rewards when authorized as admin and level exists")
    void shouldUpdateLevelWithCoinAndGemsRewardsSuccessfully() {
        CreateLevelRewardInput coinReward = new CreateLevelRewardInput(RewardType.COIN, null, 1000);
        CreateLevelRewardInput gemsReward = new CreateLevelRewardInput(RewardType.GEMS, null, 100);
        UpdateLevelInput input = new UpdateLevelInput(adminId, levelId, newTargetLevel, List.of(coinReward, gemsReward));

        doNothing().when(adminChecker).check(adminId);
        when(levelRepository.find(levelId)).thenReturn(Optional.of(mockLevel));

        UpdateLevelOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockLevel, output.level());

        verify(adminChecker, times(1)).check(adminId);
        verify(levelRepository, times(1)).find(levelId);
        verify(mockLevel, times(1)).setLevel(newTargetLevel);
        verify(mockLevel, times(1)).setRewards(anyList());
        verify(levelRepository, times(1)).save(mockLevel);
        verifyNoInteractions(cosmeticRepository);
    }

    @Test
    @DisplayName("Should successfully update a level with COSMETIC reward type when the cosmetic reference exists")
    void shouldUpdateLevelWithCosmeticRewardSuccessfully() {
        UUID cosmeticId = UUID.randomUUID();
        Cosmetic mockCosmetic = mock(Cosmetic.class);
        CreateLevelRewardInput cosmeticReward = new CreateLevelRewardInput(RewardType.COSMETIC, cosmeticId, 1);
        UpdateLevelInput input = new UpdateLevelInput(adminId, levelId, newTargetLevel, List.of(cosmeticReward));

        doNothing().when(adminChecker).check(adminId);
        when(levelRepository.find(levelId)).thenReturn(Optional.of(mockLevel));
        when(cosmeticRepository.find(cosmeticId)).thenReturn(Optional.of(mockCosmetic));

        UpdateLevelOutput output = useCase.execute(input);

        assertNotNull(output);
        verify(cosmeticRepository, times(1)).find(cosmeticId);
        verify(levelRepository, times(1)).save(mockLevel);
    }

    @Test
    @DisplayName("Should successfully update a level to have an empty list of rewards")
    void shouldUpdateLevelWithNoRewardsSuccessfully() {
        UpdateLevelInput input = new UpdateLevelInput(adminId, levelId, newTargetLevel, Collections.emptyList());

        doNothing().when(adminChecker).check(adminId);
        when(levelRepository.find(levelId)).thenReturn(Optional.of(mockLevel));

        UpdateLevelOutput output = useCase.execute(input);

        assertNotNull(output);
        verify(mockLevel, times(1)).setRewards(Collections.emptyList());
        verify(levelRepository, times(1)).save(mockLevel);
    }

    @Test
    @DisplayName("Should propagate exception and interrupt processing when admin security verification fails")
    void shouldPropagateExceptionWhenAdminCheckFails() {
        UpdateLevelInput input = new UpdateLevelInput(adminId, levelId, newTargetLevel, Collections.emptyList());

        doThrow(new SecurityException("Forbidden access")).when(adminChecker).check(adminId);

        assertThrows(SecurityException.class, () -> useCase.execute(input));

        verifyNoInteractions(levelRepository);
        verifyNoInteractions(cosmeticRepository);
    }

    @Test
    @DisplayName("Should throw LevelNotFoundException when the level identifier cannot be found in the repository")
    void shouldThrowLevelNotFoundExceptionWhenLevelDoesNotExist() {
        UpdateLevelInput input = new UpdateLevelInput(adminId, levelId, newTargetLevel, Collections.emptyList());

        doNothing().when(adminChecker).check(adminId);
        when(levelRepository.find(levelId)).thenReturn(Optional.empty());

        assertThrows(LevelNotFoundException.class, () -> useCase.execute(input));

        verify(levelRepository, never()).save(any());
        verifyNoInteractions(cosmeticRepository);
    }

    @Test
    @DisplayName("Should throw CosmeticNotFoundException when updating rewards contains a COSMETIC type but reference cannot be resolved")
    void shouldThrowCosmeticNotFoundExceptionWhenCosmeticIdIsInvalid() {
        UUID nonExistentCosmeticId = UUID.randomUUID();
        CreateLevelRewardInput cosmeticReward = new CreateLevelRewardInput(RewardType.COSMETIC, nonExistentCosmeticId, 1);
        UpdateLevelInput input = new UpdateLevelInput(adminId, levelId, newTargetLevel, List.of(cosmeticReward));

        doNothing().when(adminChecker).check(adminId);
        when(levelRepository.find(levelId)).thenReturn(Optional.of(mockLevel));
        when(cosmeticRepository.find(nonExistentCosmeticId)).thenReturn(Optional.empty());

        assertThrows(CosmeticNotFoundException.class, () -> useCase.execute(input));

        verify(levelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when the root update input reference context is null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(adminChecker);
        verifyNoInteractions(levelRepository);
        verifyNoInteractions(cosmeticRepository);
    }
}