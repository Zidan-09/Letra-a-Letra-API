package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.NicknameAlreadyInUseException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeNicknameUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemLookup itemLookup;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder auditRecorder;

    @InjectMocks
    private ChangeNicknameUseCase changeNicknameUseCase;

    private UUID userId;
    private UUID itemId;
    private ChangeNicknameInput input;
    private User user;
    private ConsumableItem nicknameItem;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        nicknameItem = ConsumableItem.create("Nickname Change", new NicknameChangeEffect());
        itemId = nicknameItem.getId();

        input = new ChangeNicknameInput(
                userId,
                "new-nick",
                itemId
        );

        user = mock(User.class);
    }

    @Test
    @DisplayName("should consume nickname item and update nickname successfully")
    void shouldUpdateNicknameSuccessfully() {
        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByNickname("new-nick"))
                .thenReturn(false);

        when(itemLookup.getById(itemId)).thenReturn(nicknameItem);
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                UserItem.restore(userId, itemId, 1, false, LocalDateTime.now(), null)
        ));

        when(user.getUsername())
                .thenReturn("new-nick");

        ChangeNicknameOutput output =
                changeNicknameUseCase.execute(input);

        assertEquals("new-nick", output.user().getUsername());

        verify(user).setUsername("new-nick");
        verify(userRepository).save(user);
        verify(inventoryRepository).deleteItemsByOwner(userId);
    }

    @Test
    @DisplayName("should throw UserNotFoundException when user does not exist")
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.find(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        verify(userRepository, never())
                .existsByNickname(anyString());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("should throw NicknameAlreadyInUseException when nickname already exists")
    void shouldThrowWhenNicknameAlreadyExists() {
        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByNickname("new-nick"))
                .thenReturn(true);

        assertThrows(
                NicknameAlreadyInUseException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        verify(userRepository, never())
                .save(any());

        verify(user, never())
                .setUsername(anyString());
    }

    @Test
    @DisplayName("should throw InvalidItemException when item is not a nickname item")
    void shouldThrowWhenItemIsNotNicknameItem() {
        ConsumableItem boost = ConsumableItem.create(
                "XP Boost",
                new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60)
        );

        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByNickname("new-nick"))
                .thenReturn(false);

        when(itemLookup.getById(itemId)).thenReturn(boost);

        assertThrows(
                InvalidItemException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        verify(userRepository, never())
                .save(any());

        verify(user, never())
                .setUsername(anyString());
    }

    @Test
    @DisplayName("should propagate exception when nickname validation fails")
    void shouldPropagateExceptionFromExistsNickname() {
        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        RuntimeException exception =
                new RuntimeException("database error");

        when(userRepository.existsByNickname("new-nick"))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> changeNicknameUseCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(userRepository, never())
                .save(any());
    }
}
