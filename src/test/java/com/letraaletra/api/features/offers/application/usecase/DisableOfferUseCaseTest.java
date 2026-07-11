package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.offers.application.input.DisableOfferInput;
import com.letraaletra.api.features.offers.application.output.DisableOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
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
class DisableOfferUseCaseTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private DisableOfferUseCase useCase;

    private UUID adminId;
    private UUID offerId;
    private DisableOfferInput input;

    @Mock
    private Offer mockOffer;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        offerId = UUID.randomUUID();
        input = new DisableOfferInput(adminId, offerId);
    }

    @Test
    @DisplayName("Should successfully disable the offer and save changes when authorized as admin and offer exists")
    void shouldDisableOfferSuccessfully() {
        doNothing().when(adminChecker).check(adminId);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(mockOffer));

        DisableOfferOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockOffer, output.offer());

        verify(adminChecker, times(1)).check(adminId);
        verify(offerRepository, times(1)).findById(offerId);
        verify(mockOffer, times(1)).disable();
        verify(offerRepository, times(1)).save(mockOffer);
    }

    @Test
    @DisplayName("Should propagate exception and halt processing when admin security verification criteria fails")
    void shouldPropagateExceptionWhenAdminCheckFails() {
        doThrow(new SecurityException("Forbidden access")).when(adminChecker).check(adminId);

        assertThrows(SecurityException.class, () -> useCase.execute(input));

        verifyNoInteractions(offerRepository);
    }

    @Test
    @DisplayName("Should throw OfferNotFoundException when the offer identifier cannot be found in the repository")
    void shouldThrowOfferNotFoundExceptionWhenOfferDoesNotExist() {
        doNothing().when(adminChecker).check(adminId);
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThrows(OfferNotFoundException.class, () -> useCase.execute(input));

        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when the root use case input parameter context itself is null")
    void shouldThrowExceptionWhenInputContextIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(adminChecker);
        verifyNoInteractions(offerRepository);
    }
}