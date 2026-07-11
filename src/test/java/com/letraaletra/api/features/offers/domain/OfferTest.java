package com.letraaletra.api.features.offers.domain;

import com.letraaletra.api.features.offers.domain.exception.InvalidOfferStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OfferTest {

    private String title;
    private CoinType coinType;
    private int price;
    private List<OfferReward> rewards;
    private LocalDateTime expiresAt;

    @BeforeEach
    void setUp() {
        title = "Promoção de Boas-Vindas";
        coinType = mock(CoinType.class);
        price = 250;
        rewards = List.of(mock(OfferReward.class));
        expiresAt = LocalDateTime.now().plusDays(7);
    }

    @Test
    @DisplayName("Should successfully instantiate a new Offer domain instance using the static factory builder method")
    void shouldCreateOfferSuccessfully() {
        Offer offer = Offer.create(title, coinType, price, rewards, true, expiresAt);

        assertNotNull(offer.getOfferId());
        assertEquals(title, offer.getTitle());
        assertEquals(coinType, offer.getCoinType());
        assertEquals(price, offer.getPrice());
        assertEquals(rewards, offer.getRewards());
        assertTrue(offer.isActive());
        assertEquals(expiresAt, offer.getExpiresAt());
        assertNotNull(offer.getCreatedAt());
        assertTrue(offer.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should successfully switch state and set active flag to false when an active offer is disabled")
    void shouldDisableActiveOfferSuccessfully() {
        Offer offer = Offer.create(title, coinType, price, rewards, true, expiresAt);

        offer.disable();

        assertFalse(offer.isActive());
    }

    @Test
    @DisplayName("Should throw InvalidOfferStatusException when attempting to disable an offer that is already inactive")
    void shouldThrowExceptionWhenDisablingAlreadyInactiveOffer() {
        Offer offer = Offer.create(title, coinType, price, rewards, false, expiresAt);

        assertThrows(InvalidOfferStatusException.class, offer::disable);
    }

    @Test
    @DisplayName("Should successfully switch state and set active flag to true when an inactive offer is enabled")
    void shouldEnableInactiveOfferSuccessfully() {
        Offer offer = Offer.create(title, coinType, price, rewards, false, expiresAt);

        offer.enable();

        assertTrue(offer.isActive());
    }

    @Test
    @DisplayName("Should throw InvalidOfferStatusException when attempting to enable an offer that is already active")
    void shouldThrowExceptionWhenEnablingAlreadyActiveOffer() {
        Offer offer = Offer.create(title, coinType, price, rewards, true, expiresAt);

        assertThrows(InvalidOfferStatusException.class, offer::enable);
    }

    @Test
    @DisplayName("Should explicitly assert full constructor parameter mapping matches fields accurately")
    void shouldMatchAllConstructorParametersFields() {
        UUID uniqueId = UUID.randomUUID();
        LocalDateTime testingCreationTimestamp = LocalDateTime.now().minusDays(1);

        Offer offer = new Offer(
                uniqueId,
                title,
                coinType,
                price,
                rewards,
                true,
                expiresAt,
                testingCreationTimestamp
        );

        assertEquals(uniqueId, offer.getOfferId());
        assertEquals(title, offer.getTitle());
        assertEquals(coinType, offer.getCoinType());
        assertEquals(price, offer.getPrice());
        assertEquals(rewards, offer.getRewards());
        assertTrue(offer.isActive());
        assertEquals(expiresAt, offer.getExpiresAt());
        assertEquals(testingCreationTimestamp, offer.getCreatedAt());
    }
}