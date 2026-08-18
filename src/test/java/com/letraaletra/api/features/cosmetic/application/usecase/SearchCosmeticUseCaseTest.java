package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.SearchCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.SearchCosmeticOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticsPage;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchCosmeticUseCase Unit Tests")
class SearchCosmeticUseCaseTest {

    @Mock
    private CosmeticRepository cosmeticRepository;

    @InjectMocks
    private SearchCosmeticUseCase useCase;

    @Captor
    private ArgumentCaptor<CosmeticsPage> pageCaptor;

    @Nested
    @DisplayName("Happy Path & Successful Search Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Should pass search term and map page parameters correctly to repository")
        @SuppressWarnings("unchecked")
        void execute_WithValidInput_ShouldDelegateToRepositoryAndReturnOutput() {
            // Arrange
            String searchTerm = "Dragon";
            int page = 1;
            int size = 10;
            Sort sort = Sort.by(Sort.Direction.ASC, "name");

            SearchCosmeticInput input = new SearchCosmeticInput(searchTerm, page, size, sort);
            Page<Cosmetic> mockPageResult = mock(Page.class);

            when(cosmeticRepository.search(eq(searchTerm), any(CosmeticsPage.class)))
                    .thenReturn(mockPageResult);

            // Act
            SearchCosmeticOutput output = useCase.execute(input);

            // Assert
            assertNotNull(output, "Output should not be null");
            assertEquals(mockPageResult, output.result(), "Output result should match repository return value");

            verify(cosmeticRepository, times(1)).search(eq(searchTerm), pageCaptor.capture());
            CosmeticsPage capturedPage = pageCaptor.getValue();
            assertNotNull(capturedPage);
            assertEquals(page, capturedPage.page());
            assertEquals(size, capturedPage.size());
            assertEquals(sort, capturedPage.sort());
        }

        @Test
        @DisplayName("Should pass unsorted Sort parameter correctly to CosmeticsPage domain value object")
        @SuppressWarnings("unchecked")
        void execute_WithUnsortedSort_ShouldConstructCosmeticsPageAccurately() {
            // Arrange
            SearchCosmeticInput input = new SearchCosmeticInput("skin", 0, 20, Sort.unsorted());
            Page<Cosmetic> mockPageResult = mock(Page.class);

            when(cosmeticRepository.search(eq("skin"), any(CosmeticsPage.class)))
                    .thenReturn(mockPageResult);

            // Act
            SearchCosmeticOutput output = useCase.execute(input);

            // Assert
            assertNotNull(output);
            assertEquals(mockPageResult, output.result());
            verify(cosmeticRepository, times(1)).search(eq("skin"), pageCaptor.capture());

            CosmeticsPage capturedPage = pageCaptor.getValue();
            assertEquals(0, capturedPage.page());
            assertEquals(20, capturedPage.size());
            assertEquals(Sort.unsorted(), capturedPage.sort());
        }
    }

    @Nested
    @DisplayName("Edge Cases & Null/Boundary Parameter Handling")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should throw NullPointerException when input object is null")
        void execute_WhenInputIsNull_ShouldThrowNullPointerException() {
            // Act & Assert
            assertThrows(
                    NullPointerException.class,
                    () -> useCase.execute(null),
                    "UseCase should fail immediately with NullPointerException when input is null"
            );

            verify(cosmeticRepository, never()).search(any(), any());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should delegate to repository when search query string is null, empty or blank")
        @SuppressWarnings("unchecked")
        void execute_WhenSearchStringIsBlankOrNull_ShouldPassSearchToRepository(String blankOrNullSearch) {
            // Arrange
            SearchCosmeticInput input = new SearchCosmeticInput(blankOrNullSearch, 0, 10, Sort.by("name"));
            Page<Cosmetic> mockPageResult = mock(Page.class);

            when(cosmeticRepository.search(eq(blankOrNullSearch), any(CosmeticsPage.class)))
                    .thenReturn(mockPageResult);

            // Act
            SearchCosmeticOutput output = useCase.execute(input);

            // Assert
            assertNotNull(output);
            assertEquals(mockPageResult, output.result());
            verify(cosmeticRepository, times(1)).search(eq(blankOrNullSearch), any(CosmeticsPage.class));
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
        @DisplayName("Should pass negative page/size values directly to CosmeticsPage construction without guard clauses")
        @SuppressWarnings("unchecked")
        void execute_WithNegativePageOrSize_ShouldPassToCosmeticsPage(int negativeValue) {
            // Arrange
            SearchCosmeticInput input = new SearchCosmeticInput("search", negativeValue, negativeValue, Sort.unsorted());
            Page<Cosmetic> mockPageResult = mock(Page.class);

            when(cosmeticRepository.search(eq("search"), any(CosmeticsPage.class)))
                    .thenReturn(mockPageResult);

            // Act
            SearchCosmeticOutput output = useCase.execute(input);

            // Assert
            assertNotNull(output);
            verify(cosmeticRepository, times(1)).search(eq("search"), pageCaptor.capture());

            CosmeticsPage capturedPage = pageCaptor.getValue();
            assertEquals(negativeValue, capturedPage.page());
            assertEquals(negativeValue, capturedPage.size());
        }
    }

    @Nested
    @DisplayName("Exception Handling & Repository Failures")
    class ExceptionTests {

        @Test
        @DisplayName("Should propagate exception when repository search throws unexpected error")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            // Arrange
            SearchCosmeticInput input = new SearchCosmeticInput("gold", 0, 10, Sort.by("name"));

            when(cosmeticRepository.search(any(), any()))
                    .thenThrow(new RuntimeException("Database timeout error"));

            // Act & Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Database timeout error", exception.getMessage());
            verify(cosmeticRepository, times(1)).search(any(), any());
        }
    }
}