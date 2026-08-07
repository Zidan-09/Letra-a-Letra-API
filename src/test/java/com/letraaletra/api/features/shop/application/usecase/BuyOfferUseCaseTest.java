package com.letraaletra.api.features.shop.application.usecase;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import com.letraaletra.api.features.offers.domain.exception.InvalidPaymentException;
import com.letraaletra.api.features.offers.domain.exception.OfferAlreadyPurchasedException;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.InsufficientBalanceException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.Balance;
import com.letraaletra.api.features.user.domain.wallet.Wallet;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;
import com.letraaletra.api.shared.application.port.RewardFactory;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RewardFactory rewardFactory;

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
        Balance mockBalanceBefore = mock(Balance.class);
        Balance mockBalanceAfter = mock(Balance.class);
        mockOffer = mock(Offer.class);

        WalletMovement walletMovement = mock(WalletMovement.class);
        OfferReward offerReward = mock(OfferReward.class);

        input = new BuyOfferInput(userId, offerId);

        // User e Wallet base setup
        lenient().when(mockUser.getUserId()).thenReturn(userId);
        lenient().when(mockUser.getWallet()).thenReturn(mockWallet);

        // Balance setup para evitar NullPointerException no Transaction.create
        lenient().when(mockBalanceBefore.getAmountFor(any())).thenReturn(1000L);
        lenient().when(mockBalanceAfter.getAmountFor(any())).thenReturn(900L);

        // WalletMovement setup
        lenient().when(walletMovement.coinType()).thenReturn(CoinType.SOFT);
        lenient().when(walletMovement.amount()).thenReturn(100);
        lenient().when(walletMovement.operation()).thenReturn(OperationType.DEBIT);
        lenient().when(walletMovement.balanceBefore()).thenReturn(mockBalanceBefore);
        lenient().when(walletMovement.balanceAfter()).thenReturn(mockBalanceAfter);

        // Offer setup
        lenient().when(mockOffer.getOfferId()).thenReturn(offerId);
        lenient().when(mockOffer.getPrice()).thenReturn(BigDecimal.valueOf(100));
        lenient().when(mockOffer.getRewards()).thenReturn(List.of(offerReward));
        lenient().when(mockOffer.isRepeatable()).thenReturn(false);

        // Rewards setup (SoftCoinsReward chama mockWallet.add por baixo)
        WalletMovement rewardMovement = mock(WalletMovement.class);
        lenient().when(rewardMovement.coinType()).thenReturn(CoinType.SOFT);
        lenient().when(rewardMovement.amount()).thenReturn(100);
        lenient().when(rewardMovement.operation()).thenReturn(OperationType.CREDIT);
        lenient().when(rewardMovement.balanceBefore()).thenReturn(mockBalanceBefore);
        lenient().when(rewardMovement.balanceAfter()).thenReturn(mockBalanceAfter);

        lenient().when(mockWallet.add(any(), anyInt())).thenReturn(rewardMovement);
        lenient().when(offerReward.reward()).thenReturn(new SoftCoinsReward(100));

        lenient().when(mockWallet.remove(any(), anyInt())).thenReturn(walletMovement);

        lenient().when(transactionRepository.existsOfferPurchase(any(), any())).thenReturn(false);
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

        verify(mockWallet).remove(CoinType.SOFT, 100);
        verify(userRepository).save(mockUser);
        verify(transactionRepository, atLeastOnce()).save(any());
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
    @DisplayName("should throw OfferAlreadyPurchasedException when offer is not repeatable and user already purchased it")
    void shouldThrowOfferAlreadyPurchasedExceptionWhenUserAlreadyPurchasedOffer() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));

        when(mockOffer.isActive()).thenReturn(true);
        when(mockOffer.getCoinType()).thenReturn(CoinType.SOFT);
        when(mockOffer.isRepeatable()).thenReturn(false);

        when(transactionRepository.existsOfferPurchase(userId, input.offerId()))
                .thenReturn(true);

        assertThrows(
                OfferAlreadyPurchasedException.class,
                () -> useCase.execute(input)
        );

        verify(mockWallet, never()).remove(any(), anyInt());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should not check previous purchases when offer is repeatable")
    void shouldNotCheckPreviousPurchasesWhenOfferIsRepeatable() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));

        when(mockOffer.isActive()).thenReturn(true);
        when(mockOffer.getCoinType()).thenReturn(CoinType.SOFT);
        when(mockOffer.isRepeatable()).thenReturn(true);

        useCase.execute(input);

        verify(transactionRepository, never())
                .existsOfferPurchase(any(), any());
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

        verify(mockWallet, never()).remove(any(), any(Integer.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InsufficientBalanceException when user balance is insufficient")
    void shouldPropagateExceptionWhenWalletPaymentFails() {
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockOffer.isActive()).thenReturn(true);
        when(mockOffer.getCoinType()).thenReturn(CoinType.SOFT);

        when(mockWallet.remove(CoinType.SOFT, 100))
                .thenThrow(new InsufficientBalanceException());

        assertThrows(InsufficientBalanceException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any());
    }
}