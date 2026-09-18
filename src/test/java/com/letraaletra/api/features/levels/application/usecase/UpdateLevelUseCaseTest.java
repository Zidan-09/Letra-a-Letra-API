package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.input.UpdateLevelInput;
import com.letraaletra.api.features.levels.application.output.UpdateLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.exception.LevelAlreadyExistsException;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private ItemRepository itemRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private UpdateLevelUseCase useCase;

    private AuthenticatedUser principal;
    private final PermissionKey key = PermissionKey.LEVELS;
    private final PermissionAction action = PermissionAction.EDIT;
    private UUID levelId;
    private int newTargetLevel;
    private Level mockLevel;

    @BeforeEach
    void setUp() {
        principal = mock(AuthenticatedUser.class);
        levelId = UUID.randomUUID();
        newTargetLevel = 15;
        mockLevel = mock(Level.class);
    }

    @Test
    @DisplayName("Should successfully update a level with COIN and GEMS rewards when authorized as admin and level exists")
    void shouldUpdateLevelWithCoinAndGemsRewardsSuccessfully() {
        CreateLevelRewardInput coinReward = new CreateLevelRewardInput(RewardType.COIN, null, 1000);
        CreateLevelRewardInput gemsReward = new CreateLevelRewardInput(RewardType.GEMS, null, 100);
        UpdateLevelInput input = new UpdateLevelInput(principal, levelId, newTargetLevel, List.of(coinReward, gemsReward));

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.find(levelId)).thenReturn(Optional.of(mockLevel));
        when(levelRepository.findByLevel(input.level()))
                .thenReturn(Optional.empty());

        UpdateLevelOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockLevel, output.level());

        verify(adminChecker, times(1)).check(principal, key, action);
        verify(levelRepository, times(1)).find(levelId);
        verify(mockLevel, times(1)).setLevel(newTargetLevel);
        verify(mockLevel, times(1)).setRewards(anyList());
        verify(levelRepository, times(1)).save(mockLevel);
        verifyNoInteractions(itemRepository);
    }

    @Test
    @DisplayName("Should successfully update a level with ITEM reward type when the item exists")
    void shouldUpdateLevelWithItemRewardSuccessfully() {
        UUID definitionId = UUID.randomUUID();
        Item item = mock(Item.class);
        when(item.getId()).thenReturn(definitionId);
        CreateLevelRewardInput itemReward = new CreateLevelRewardInput(RewardType.ITEM, definitionId, 2);
        UpdateLevelInput input = new UpdateLevelInput(principal, levelId, newTargetLevel, List.of(itemReward));

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.find(levelId)).thenReturn(Optional.of(mockLevel));
        when(levelRepository.findByLevel(input.level()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(definitionId)).thenReturn(Optional.of(item));

        UpdateLevelOutput output = useCase.execute(input);

        assertNotNull(output);
        verify(itemRepository, times(1)).findById(definitionId);

        ArgumentCaptor<List<LevelReward>> rewardsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockLevel, times(1)).setRewards(rewardsCaptor.capture());
        assertEquals(1, rewardsCaptor.getValue().size());
        assertEquals(new ItemGrantReward(definitionId, 2), rewardsCaptor.getValue().get(0).reward());
        verify(levelRepository, times(1)).save(mockLevel);
    }

    @Test
    @DisplayName("Should successfully update a level to have an empty list of rewards")
    void shouldUpdateLevelWithNoRewardsSuccessfully() {
        UpdateLevelInput input = new UpdateLevelInput(principal, levelId, newTargetLevel, Collections.emptyList());

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.find(levelId)).thenReturn(Optional.of(mockLevel));
        when(levelRepository.findByLevel(input.level()))
                .thenReturn(Optional.empty());

        UpdateLevelOutput output = useCase.execute(input);

        assertNotNull(output);
        verify(mockLevel, times(1)).setRewards(Collections.emptyList());
        verify(levelRepository, times(1)).save(mockLevel);
    }

    @Test
    @DisplayName("Should propagate exception and interrupt processing when admin security verification fails")
    void shouldPropagateExceptionWhenAdminCheckFails() {
        UpdateLevelInput input = new UpdateLevelInput(principal, levelId, newTargetLevel, Collections.emptyList());

        doThrow(new SecurityException("Forbidden access")).when(adminChecker).check(principal, key, action);

        assertThrows(SecurityException.class, () -> useCase.execute(input));

        verifyNoInteractions(levelRepository);
        verifyNoInteractions(itemRepository);
    }

    @Test
    @DisplayName("Should throw LevelNotFoundException when the level identifier cannot be found in the repository")
    void shouldThrowLevelNotFoundExceptionWhenLevelDoesNotExist() {
        UpdateLevelInput input = new UpdateLevelInput(principal, levelId, newTargetLevel, Collections.emptyList());

        doNothing().when(adminChecker).check(principal, key, action);
        when(levelRepository.find(levelId)).thenReturn(Optional.empty());

        assertThrows(LevelNotFoundException.class, () -> useCase.execute(input));

        verify(levelRepository, never()).save(any());
        verifyNoInteractions(itemRepository);
    }

    @Test
    @DisplayName("Should throw RuntimeException when the root update input reference context is null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(adminChecker);
        verifyNoInteractions(levelRepository);
        verifyNoInteractions(itemRepository);
    }

    @Test
    @DisplayName("Should throw LevelAlreadyExistsException when another level already has the requested level number")
    void shouldThrowLevelAlreadyExistsException() {
        UpdateLevelInput input = new UpdateLevelInput(
                principal,
                levelId,
                newTargetLevel,
                Collections.emptyList()
        );

        UUID anotherLevelId = UUID.randomUUID();

        Level currentLevel = mock(Level.class);
        Level existingLevel = mock(Level.class);

        doNothing().when(adminChecker).check(principal, key, action);

        when(levelRepository.find(levelId))
                .thenReturn(Optional.of(currentLevel));

        when(levelRepository.findByLevel(newTargetLevel))
                .thenReturn(Optional.of(existingLevel));

        when(existingLevel.getLevelId())
                .thenReturn(anotherLevelId);

        assertThrows(LevelAlreadyExistsException.class,
                () -> useCase.execute(input));

        verify(levelRepository, never()).save(any());
    }
}