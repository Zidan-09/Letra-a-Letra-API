package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.store.application.input.BuyOfferInput;
import com.letraaletra.api.features.store.application.output.BuyOfferOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.store.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.features.user.application.service.UnlockCosmeticService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.InsufficientBalanceException;
import com.letraaletra.api.features.user.domain.wallet.CoinType;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyOfferUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreOfferRepository storeOfferRepository;

    @Mock
    private UnlockCosmeticService unlockCosmeticService;

    @InjectMocks
    private BuyOfferUseCase useCase;

    private BuyOfferInput input;
    private User mockUser;
    private Wallet mockWallet;
    private StoreOffer mockOffer;

    @BeforeEach
    void setUp() {
        input = new BuyOfferInput("user-123", "offer-456");

        mockUser = mock(User.class);
        mockWallet = mock(Wallet.class);
        mockOffer = mock(StoreOffer.class);
        Cosmetic mockCosmetic = mock(Cosmetic.class);

        lenient().when(mockUser.getId()).thenReturn("user-123");
        lenient().when(mockUser.getWallet()).thenReturn(mockWallet);
        lenient().when(mockOffer.getCosmetic()).thenReturn(mockCosmetic);
        lenient().when(mockCosmetic.getId()).thenReturn("cosmetic-789");
        lenient().when(mockOffer.getCoinType()).thenReturn(CoinType.HARD);
        lenient().when(mockOffer.getPrice()).thenReturn(100);
    }

    @Test
    @DisplayName("should buy an offer correctly when all data is valid")
    void shouldBuyOfferWithSuccess() {
        when(userRepository.find(input.userId())).thenReturn(Optional.of(mockUser));
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(mockOffer.isActive()).thenReturn(true);

        BuyOfferOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockOffer, output.offer());

        verify(mockWallet, times(1)).pay(CoinType.HARD, 100);
        verify(unlockCosmeticService, times(1)).execute("cosmetic-789", "user-123");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("should throw a UserNotFoundException when user don't exists")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.find(input.userId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verifyNoInteractions(storeOfferRepository, unlockCosmeticService);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an OfferNotFoundException when offer don't exists")
    void shouldThrowOfferNotFoundExceptionWhenOfferDoesNotExist() {
        when(userRepository.find(input.userId())).thenReturn(Optional.of(mockUser));
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.empty());

        assertThrows(OfferNotFoundException.class, () -> useCase.execute(input));

        verifyNoInteractions(unlockCosmeticService);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InvalidOfferStatusException when offer is not active")
    void shouldThrowInvalidOfferStatusExceptionWhenOfferIsNotActive() {
        when(userRepository.find(input.userId())).thenReturn(Optional.of(mockUser));
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(mockOffer.isActive()).thenReturn(false);

        assertThrows(InvalidOfferStatusException.class, () -> useCase.execute(input));

        verifyNoInteractions(mockWallet, unlockCosmeticService);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InsufficientBalanceException when user balance is insufficient")
    void shouldPropagateExceptionWhenWalletPaymentFails() {
        when(userRepository.find(input.userId())).thenReturn(Optional.of(mockUser));
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(mockOffer.isActive()).thenReturn(true);

        doThrow(new InsufficientBalanceException())
                .when(mockWallet).pay(CoinType.HARD, 100);

        assertThrows(InsufficientBalanceException.class, () -> useCase.execute(input));

        verifyNoInteractions(unlockCosmeticService);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should stop the salving user process if unlock service throws an exception")
    void shouldNotSaveUserWhenUnlockCosmeticServiceFails() {
        when(userRepository.find(input.userId())).thenReturn(Optional.of(mockUser));
        when(storeOfferRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(mockOffer.isActive()).thenReturn(true);

        doThrow(new UserNotFoundException())
                .when(unlockCosmeticService).execute(anyString(), anyString());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(mockUser);
    }
}