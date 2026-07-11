package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.offers.application.input.FindOfferInput;
import com.letraaletra.api.features.offers.application.output.FindOfferOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
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
class FindOfferUseCaseTest {

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private FindOfferUseCase useCase;

    private UUID offerId;
    private FindOfferInput input;

    @Mock
    private Offer mockOffer;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();
        input = new FindOfferInput(offerId);
    }

    @Test
    @DisplayName("Should successfully return FindOfferOutput containing the offer when repository finds the record")
    void shouldFindOfferSuccessfully() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(mockOffer));

        FindOfferOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockOffer, output.offer());

        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw OfferNotFoundException when the offer identifier cannot be found in the repository")
    void shouldThrowOfferNotFoundExceptionWhenOfferDoesNotExist() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThrows(OfferNotFoundException.class, () -> useCase.execute(input));

        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw RuntimeException when the root use case input parameter context itself is null")
    void shouldThrowExceptionWhenInputContextIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(offerRepository);
    }
}