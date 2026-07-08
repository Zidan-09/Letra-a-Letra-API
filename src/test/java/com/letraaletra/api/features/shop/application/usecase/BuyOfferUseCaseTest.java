package com.letraaletra.api.features.shop.application.usecase;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.offers.domain.exception.InvalidPaymentException;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.offers.domain.rewards.SoftCoinsReward;
import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.InsufficientBalanceException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyOfferUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private BuyOfferUseCase useCase;

    private BuyOfferInput input;
    private UUID userId;
    private User mockUser;
    private Wallet mockWallet;
    private Offer mockOffer;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        mockUser = mock(User.class);
        mockWallet = mock(Wallet.class);
        mockOffer = mock(Offer.class);

        OfferReward mockOfferReward = mock(OfferReward.class);

        input = new BuyOfferInput(userId, offerId);

        lenient().when(mockUser.getId()).thenReturn(userId);
        lenient().when(mockUser.getWallet()).thenReturn(mockWallet);

        lenient().when(mockOffer.getPrice()).thenReturn(100);
        lenient().when(mockOffer.getRewards()).thenReturn(List.of(mockOfferReward));

        lenient().when(mockOfferReward.reward()).thenReturn(new SoftCoinsReward(100));
    }

    @Test
    @DisplayName("should buy an offer correctly when all data is valid")
    void shouldBuyOfferWithSuccess() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockOffer.isActive()).thenReturn(true);
        when(mockOffer.getCoinType()).thenReturn(CoinType.SOFT);

        BuyOfferOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockOffer, output.offer());

        verify(mockWallet).pay(CoinType.SOFT, 100);
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("should throw an OfferNotFoundException when offer doesn't exist")
    void shouldThrowOfferNotFoundExceptionWhenOfferDoesNotExist() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.empty());

        assertThrows(OfferNotFoundException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any());
        verifyNoInteractions(mockWallet);
    }

    @Test
    @DisplayName("should throw an UserNotFoundException when user doesn't exist")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any());
        verifyNoInteractions(mockWallet);
    }

    @Test
    @DisplayName("should throw an InvalidOfferStatusException when offer is not active")
    void shouldThrowInvalidOfferStatusExceptionWhenOfferIsNotActive() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockOffer.isActive()).thenReturn(false);

        assertThrows(InvalidOfferStatusException.class, () -> useCase.execute(input));

        verifyNoInteractions(mockWallet);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InvalidPaymentException when CoinType is REAL")
    void shouldThrowInvalidPaymentExceptionWhenCoinTypeIsReal() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockOffer.isActive()).thenReturn(true);
        when(mockOffer.getCoinType()).thenReturn(CoinType.REAL);

        assertThrows(InvalidPaymentException.class, () -> useCase.execute(input));

        verify(mockWallet, never()).pay(any(), anyInt());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InsufficientBalanceException when user balance is insufficient")
    void shouldPropagateExceptionWhenWalletPaymentFails() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockOffer.isActive()).thenReturn(true);
        when(mockOffer.getCoinType()).thenReturn(CoinType.SOFT);

        doThrow(new InsufficientBalanceException())
                .when(mockWallet).pay(CoinType.SOFT, 100);

        assertThrows(InsufficientBalanceException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any());
    }
}