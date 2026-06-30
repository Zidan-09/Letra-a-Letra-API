package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCosmeticsUseCaseTest {

    @Mock
    private CosmeticRepository cosmeticRepository;

    private GetCosmeticsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetCosmeticsUseCase(cosmeticRepository);
    }

    @Test
    void shouldReturnCosmeticsFromRepository() {
        GetCosmeticsInput input = new GetCosmeticsInput(
                0,
                10,
                    Sort.unsorted()
        );

        Page<Cosmetic> expectedPage = new PageImpl<>(List.of());

        when(cosmeticRepository.get(input))
                .thenReturn(expectedPage);

        GetCosmeticsOutput output = useCase.execute(input);

        verify(cosmeticRepository).get(input);
        verifyNoMoreInteractions(cosmeticRepository);

        assertSame(expectedPage, output.cosmetics());
        assertEquals(0, output.cosmetics().getNumber());
        assertEquals(0, output.cosmetics().getContent().size());
    }
}