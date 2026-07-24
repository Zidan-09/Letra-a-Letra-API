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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    @DisplayName("Should successfully return populated offers page")
    void shouldReturnPopulatedOffersOutputSuccessfully() {

        Page<Offer> expectedOffers = new PageImpl<>(
                List.of(mockOffer1, mockOffer2),
                PageRequest.of(0, 20),
                2
        );

        when(offerRepository.get(mockInput))
                .thenReturn(expectedOffers);

        GetOffersOutput output = useCase.execute(mockInput);

        assertNotNull(output);
        assertEquals(expectedOffers, output.offers());
        assertEquals(2, output.offers().getContent().size());

        verify(offerRepository).get(mockInput);
    }

    @Test
    @DisplayName("Should successfully return empty offers page")
    void shouldReturnEmptyOffersOutputSuccessfully() {

        when(offerRepository.get(mockInput))
                .thenReturn(Page.empty());

        GetOffersOutput output = useCase.execute(mockInput);

        assertNotNull(output);
        assertTrue(output.offers().isEmpty());

        verify(offerRepository).get(mockInput);
    }

    @Test
    @DisplayName("Should propagate repository exceptions")
    void shouldPropagateRepositoryExceptions() {

        when(offerRepository.get(mockInput))
                .thenThrow(new RuntimeException("Database connectivity failure"));

        assertThrows(
                RuntimeException.class,
                () -> useCase.execute(mockInput)
        );
    }
}