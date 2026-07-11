package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOffersUseCaseTest {

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private GetOffersUseCase useCase;

    private GetOffersInput mockInput;
    @Mock
    private Offer mockOffer1;
    @Mock
    private Offer mockOffer2;

    @BeforeEach
    void setUp() {
        mockInput = mock(GetOffersInput.class);
    }

    @Test
    @DisplayName("Should successfully return GetOffersOutput containing a populated list of offers when repository finds records")
    void shouldReturnPopulatedOffersOutputSuccessfully() {
        List<Offer> expectedOffers = List.of(mockOffer1, mockOffer2);
        when(offerRepository.get(mockInput)).thenReturn(expectedOffers);

        GetOffersOutput output = useCase.execute(mockInput);

        assertNotNull(output);
        assertEquals(expectedOffers, output.offers()); // Assumindo record component ou getter .offers()
        assertEquals(2, output.offers().size());
        verify(offerRepository, times(1)).get(mockInput);
    }

    @Test
    @DisplayName("Should successfully return GetOffersOutput containing an empty list when no offers match the input criteria")
    void shouldReturnEmptyOffersOutputSuccessfully() {
        when(offerRepository.get(mockInput)).thenReturn(Collections.emptyList());

        GetOffersOutput output = useCase.execute(mockInput);

        assertNotNull(output);
        assertTrue(output.offers().isEmpty());
        verify(offerRepository, times(1)).get(mockInput);
    }

    @Test
    @DisplayName("Should propagate any runtime exception thrown by the repository layer execution context")
    void shouldPropagateRepositoryExceptions() {
        when(offerRepository.get(mockInput)).thenThrow(new RuntimeException("Database connectivity failure"));

        assertThrows(RuntimeException.class, () -> useCase.execute(mockInput));
    }
}