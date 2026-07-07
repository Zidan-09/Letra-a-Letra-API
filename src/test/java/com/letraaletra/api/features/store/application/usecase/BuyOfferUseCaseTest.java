package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.store.application.input.BuyOfferInput;
import com.letraaletra.api.features.store.application.output.BuyOfferOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.store.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.InsufficientBalanceException;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.wallet.CoinType;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyOfferUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreOfferRepository storeOfferRepository;

    @InjectMocks
    private BuyOfferUseCase useCase;

    private BuyOfferInput input;
    private UUID userId;
    private User mockUser;
    private Wallet mockWallet;
    private Inventory mockInventory;
    private StoreOffer mockOffer;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        mockUser = mock(User.class);
        mockWallet = mock(Wallet.class);
        mockInventory = mock(Inventory.class);
        mockOffer = mock(StoreOffer.class);
        Cosmetic mockCosmetic = mock(Cosmetic.class);

        input = new BuyOfferInput(userId, offerId);

        lenient().when(mockUser.getId()).thenReturn(userId);
        lenient().when(mockUser.getWallet()).thenReturn(mockWallet);
        lenient().when(mockOffer.getCosmetic()).thenReturn(mockCosmetic);
        lenient().when(mockCosmetic.getId()).thenReturn(UUID.randomUUID());
        lenient().when(mockOffer.getCoinType()).thenReturn(CoinType.HARD);
        lenient().when(mockOffer.getPrice()).thenReturn(100);
        lenient().when(mockUser.getInventory()).thenReturn(mockInventory);
    }

    @Test
    @DisplayName("should buy an offer correctly when all data is valid")
    void shouldBuyOfferWithSuccess() {
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockOffer.isActive()).thenReturn(true);

        BuyOfferOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockOffer, output.offer());

        verify(mockWallet, times(1)).pay(CoinType.HARD, 100);
        verify(userRepository, times(1)).save(mockUser);
        verify(mockInventory, times(1)).unlock(mockOffer.getCosmetic());
        verify(mockUser, times(1)).getInventory();
    }

    @Test
    @DisplayName("should throw an OfferNotFoundException when offer don't exists")
    void shouldThrowOfferNotFoundExceptionWhenOfferDoesNotExist() {
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.empty());

        assertThrows(OfferNotFoundException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InvalidOfferStatusException when offer is not active")
    void shouldThrowInvalidOfferStatusExceptionWhenOfferIsNotActive() {
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockOffer.isActive()).thenReturn(false);

        assertThrows(InvalidOfferStatusException.class, () -> useCase.execute(input));

        verifyNoInteractions(mockWallet);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InsufficientBalanceException when user balance is insufficient")
    void shouldPropagateExceptionWhenWalletPaymentFails() {
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockOffer.isActive()).thenReturn(true);

        doThrow(new InsufficientBalanceException())
                .when(mockWallet).pay(CoinType.HARD, 100);

        assertThrows(InsufficientBalanceException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any());
    }
}