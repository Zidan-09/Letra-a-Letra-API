package com.letraaletra.api.features.shop.application.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import com.letraaletra.api.features.shop.application.input.BuyOfferInput;
import com.letraaletra.api.features.shop.application.output.BuyOfferOutput;
import com.letraaletra.api.features.shop.application.port.ShopPurchasePort;
import com.letraaletra.api.features.shop.application.port.ShopPurchaseResult;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.wallet.exception.InsufficientBalanceException;

@ExtendWith(MockitoExtension.class)
class BuyOfferUseCaseTest {

    @Mock
    private ShopPurchasePort purchasePort;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BusinessAuditRecorder auditRecorder;

    @InjectMocks
    private BuyOfferUseCase useCase;

    private BuyOfferInput input;
    private UUID userId;
    private UUID offerId;
    private User mockUser;
    private Offer mockOffer;
    private ShopPurchaseResult mockPurchaseResult;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        offerId = UUID.randomUUID();
        input = new BuyOfferInput(userId, offerId);

        mockUser = mock(User.class);
        lenient().when(mockUser.getUserId()).thenReturn(userId);
        lenient().when(mockUser.getUsername()).thenReturn("testuser");

        mockOffer = mock(Offer.class);
        OfferReward offerReward = mock(OfferReward.class);
        lenient().when(offerReward.reward()).thenReturn(new SoftCoinsReward(50));

        lenient().when(mockOffer.getOfferId()).thenReturn(offerId);
        lenient().when(mockOffer.getPrice()).thenReturn(BigDecimal.valueOf(100));
        lenient().when(mockOffer.getCoinType()).thenReturn(CoinType.SOFT);
        lenient().when(mockOffer.getRewards()).thenReturn(List.of(offerReward));

        mockPurchaseResult = new ShopPurchaseResult(
                List.of(UUID.randomUUID(), UUID.randomUUID()),
                950L,
                0L
        );
    }

    @Test
    @DisplayName("should buy an offer correctly when all data is valid")
    void shouldBuyOfferWithSuccess() {
        when(purchasePort.purchase(input.auth(), input.offerId())).thenReturn(mockPurchaseResult);
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(input.auth())).thenReturn(Optional.of(mockUser));

        BuyOfferOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockOffer, output.offer());

        verify(purchasePort).purchase(input.auth(), input.offerId());
        verify(auditRecorder, atLeastOnce()).record(any());
    }

    @Test
    @DisplayName("débito e créditos da mesma compra devem compartilhar operationId e vincular transactionId")
    void purchaseEventsShouldShareOperationIdAndReferenceTransactions() {
        when(purchasePort.purchase(input.auth(), input.offerId())).thenReturn(mockPurchaseResult);
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(input.auth())).thenReturn(Optional.of(mockUser));

        useCase.execute(input);

        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditRecorder, atLeastOnce()).record(eventCaptor.capture());

        List<AuditEvent> events = eventCaptor.getAllValues();

        assertFalse(events.isEmpty());
        assertTrue(events.stream().allMatch(e -> e.operationId() != null));

        UUID sharedOperationId = events.get(0).operationId();
        assertTrue(events.stream().allMatch(e -> e.operationId().equals(sharedOperationId)));

        assertTrue(events.stream().anyMatch(e -> e.eventType() == AuditEventType.WALLET_DEBITED));
        assertTrue(events.stream().anyMatch(e -> e.eventType() == AuditEventType.WALLET_CREDITED));
        assertTrue(events.stream().allMatch(e -> e.transactionId() != null));
        assertEquals(AuditCategory.ECONOMY, events.get(0).category());
    }

    @Test
    @DisplayName("should throw an OfferNotFoundException when offer doesn't exist")
    void shouldThrowOfferNotFoundExceptionWhenOfferDoesNotExist() {
        when(purchasePort.purchase(input.auth(), input.offerId())).thenReturn(mockPurchaseResult);
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.empty());

        assertThrows(OfferNotFoundException.class, () -> useCase.execute(input));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(auditRecorder);
    }

    @Test
    @DisplayName("should throw an UserNotFoundException when user doesn't exist")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(purchasePort.purchase(input.auth(), input.offerId())).thenReturn(mockPurchaseResult);
        when(offerRepository.findById(input.offerId())).thenReturn(Optional.of(mockOffer));
        when(userRepository.find(input.auth())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verifyNoInteractions(auditRecorder);
    }

    @Test
    @DisplayName("should propagate exception when purchase in port fails due to insufficient balance")
    void shouldPropagateExceptionWhenPurchasePortFails() {
        when(purchasePort.purchase(input.auth(), input.offerId()))
                .thenThrow(new InsufficientBalanceException());

        assertThrows(InsufficientBalanceException.class, () -> useCase.execute(input));

        verifyNoInteractions(offerRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(auditRecorder);
    }
}