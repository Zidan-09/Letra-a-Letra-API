package com.letraaletra.api.features.store.application.usecase;

import com.letraaletra.api.features.store.application.output.GetActiveOffersOutput;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetActiveOffersUseCaseTest {
    @Mock
    private StoreOfferRepository repository;

    @InjectMocks
    private GetActiveOffersUseCase useCase;

    @Test
    @DisplayName("should get an list of Offers")
    void shouldGetAnList() {
        StoreOffer offer1 = mock(StoreOffer.class);
        StoreOffer offer2 = mock(StoreOffer.class);

        when(repository.getActiveOffers())
                .thenReturn(List.of(offer1, offer2));

        GetActiveOffersOutput output = useCase.execute();

        assertFalse(output.offers().isEmpty());
        assertEquals(2, output.offers().size());
    }

    @Test
    @DisplayName("should get an empty list of Offers")
    void shouldGetAnEmptyList() {
        when(repository.getActiveOffers())
                .thenReturn(List.of());

        GetActiveOffersOutput output = useCase.execute();

        assertTrue(output.offers().isEmpty());
    }
}