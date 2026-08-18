package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.exception.LevelAlreadyExistsException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.offers.domain.RewardType;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.RewardFactory;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLevelUseCaseTest {

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private AdminChecker adminChecker;

    @Mock
    private RewardFactory rewardFactory;

    @InjectMocks
    private CreateLevelUseCase useCase;

    @Captor
    private ArgumentCaptor<Level> levelCaptor;

    private AuthenticatedUser principal;
    private final PermissionKey key = PermissionKey.LEVELS;
    private final PermissionAction action = PermissionAction.CREATE;
    private int targetLevel;

    @BeforeEach
    void setUp() {
        principal = mock(AuthenticatedUser.class);
        targetLevel = 10;
    }

    @Test
    @DisplayName("Should successfully create a level with COIN reward type when authorized as admin")
    void shouldCreateLevelWithCoinRewardSuccessfully() {
        CreateLevelRewardInput coinRewardInput = new CreateLevelRewardInput(RewardType.COIN, null, 500);
        CreateLevelInput input = new CreateLevelInput(principal, targetLevel, List.of(coinRewardInput));

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.existsByLevel(input.level())).thenReturn(false);
        when(rewardFactory.create(RewardType.COIN, 500, null)).thenReturn(new SoftCoinsReward(500));

        CreateLevelOutput output = useCase.execute(input);

        assertNotNull(output);
        verify(adminChecker, times(1)).check(principal, key, action);
        verify(rewardFactory, times(1)).create(RewardType.COIN, 500, null);
        verify(levelRepository, times(1)).save(levelCaptor.capture());

        Level savedLevel = levelCaptor.getValue();
        assertNotNull(savedLevel);
        assertEquals(targetLevel, savedLevel.getLevel());
        assertEquals(1, savedLevel.getRewards().size());
    }

    @Test
    @DisplayName("Should successfully create a level with GEMS reward type when authorized as admin")
    void shouldCreateLevelWithGemsRewardSuccessfully() {
        CreateLevelRewardInput gemsRewardInput = new CreateLevelRewardInput(RewardType.GEMS, null, 50);
        CreateLevelInput input = new CreateLevelInput(principal, targetLevel, List.of(gemsRewardInput));

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.existsByLevel(input.level())).thenReturn(false);
        when(rewardFactory.create(RewardType.GEMS, 50, null)).thenReturn(new HardGemsReward(50));

        useCase.execute(input);

        verify(rewardFactory, times(1)).create(RewardType.GEMS, 50, null);
        verify(levelRepository, times(1)).save(any(Level.class));
    }

    @Test
    @DisplayName("Should successfully create a level with COSMETIC reward type when the reference exists")
    void shouldCreateLevelWithCosmeticRewardSuccessfully() {
        UUID cosmeticId = UUID.randomUUID();
        CreateLevelRewardInput cosmeticRewardInput = new CreateLevelRewardInput(RewardType.COSMETIC, cosmeticId, 1);
        CreateLevelInput input = new CreateLevelInput(principal, targetLevel, List.of(cosmeticRewardInput));

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.existsByLevel(input.level())).thenReturn(false);
        when(rewardFactory.create(RewardType.COSMETIC, 1, cosmeticId)).thenReturn(new SoftCoinsReward(1));

        useCase.execute(input);

        verify(rewardFactory, times(1)).create(RewardType.COSMETIC, 1, cosmeticId);
        verify(levelRepository, times(1)).save(any(Level.class));
    }

    @Test
    @DisplayName("Should successfully create a level with multiple mixed reward types")
    void shouldCreateLevelWithMixedRewardsSuccessfully() {
        UUID cosmeticId = UUID.randomUUID();
        CreateLevelRewardInput coinReward = new CreateLevelRewardInput(RewardType.COSMETIC, null, 100);
        CreateLevelRewardInput gemsReward = new CreateLevelRewardInput(RewardType.GEMS, null, 10);
        CreateLevelRewardInput cosmeticReward = new CreateLevelRewardInput(RewardType.COSMETIC, cosmeticId, 1);

        CreateLevelInput input = new CreateLevelInput(principal, targetLevel, List.of(coinReward, gemsReward, cosmeticReward));

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.existsByLevel(input.level())).thenReturn(false);
        when(rewardFactory.create(any(), anyInt(), any())).thenReturn(new SoftCoinsReward(10));

        useCase.execute(input);

        verify(rewardFactory, times(3)).create(any(), anyInt(), any());
        verify(levelRepository, times(1)).save(any(Level.class));
    }

    @Test
    @DisplayName("Should successfully create a level with an empty list of rewards")
    void shouldCreateLevelWithNoRewardsSuccessfully() {
        CreateLevelInput input = new CreateLevelInput(principal, targetLevel, Collections.emptyList());

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.existsByLevel(input.level())).thenReturn(false);

        useCase.execute(input);

        verify(rewardFactory, never()).create(any(), anyInt(), any());
        verify(levelRepository, times(1)).save(any(Level.class));
    }

    @Test
    @DisplayName("Should propagate exception when admin security verification criteria fails")
    void shouldPropagateExceptionWhenAdminCheckFails() {
        CreateLevelInput input = new CreateLevelInput(principal, targetLevel, Collections.emptyList());

        doThrow(new SecurityException("Unauthorized access")).when(adminChecker).check(principal, key, action);

        assertThrows(SecurityException.class, () -> useCase.execute(input));

        verifyNoInteractions(levelRepository);
        verifyNoInteractions(rewardFactory);
    }

    @Test
    @DisplayName("Should throw LevelAlreadyExistsException when exists a level with the same value that the input")
    void shouldThrowLevelAlreadyExistsException() {
        CreateLevelInput input = new CreateLevelInput(principal, targetLevel, Collections.emptyList());

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.existsByLevel(input.level())).thenReturn(true);

        assertThrows(LevelAlreadyExistsException.class, () -> useCase.execute(input));

        verify(levelRepository, never()).save(any());
        verifyNoInteractions(rewardFactory);
    }

    @Test
    @DisplayName("Should propagate exception when RewardFactory fails to create reward")
    void shouldPropagateExceptionWhenRewardFactoryFails() {
        UUID invalidRef = UUID.randomUUID();
        CreateLevelRewardInput invalidRewardInput = new CreateLevelRewardInput(RewardType.COSMETIC, invalidRef, 1);
        CreateLevelInput input = new CreateLevelInput(principal, targetLevel, List.of(invalidRewardInput));

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.existsByLevel(input.level())).thenReturn(false);
        when(rewardFactory.create(RewardType.COSMETIC, 1, invalidRef))
                .thenThrow(new IllegalArgumentException("Invalid reward reference"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));

        verify(levelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail or throw exception when root payload context structure parameters are null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(adminChecker);
        verifyNoInteractions(levelRepository);
        verifyNoInteractions(rewardFactory);
    }
}