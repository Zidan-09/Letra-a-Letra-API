package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.FindCosmeticByNameInput;
import com.letraaletra.api.features.cosmetic.application.output.FindCosmeticByNameOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindCosmeticByNameUseCase Unit Tests")
class FindCosmeticByNameUseCaseTest {

    @Mock
    private CosmeticRepository cosmeticRepository;

    @InjectMocks
    private FindCosmeticByNameUseCase useCase;

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Should return output containing cosmetic when cosmetic is found by name")
        void execute_WhenCosmeticExists_ShouldReturnOutputWithCosmetic() {
            // Arrange
            String cosmeticName = "Golden Skin";
            FindCosmeticByNameInput input = new FindCosmeticByNameInput(cosmeticName);
            Cosmetic expectedCosmetic = mock(Cosmetic.class);

            when(cosmeticRepository.findByName(cosmeticName))
                    .thenReturn(Optional.of(expectedCosmetic));

            // Act
            FindCosmeticByNameOutput output = useCase.execute(input);

            // Assert
            assertNotNull(output, "Output should not be null");
            assertEquals(expectedCosmetic, output.cosmetic(), "Returned cosmetic should match repository response");
            verify(cosmeticRepository, times(1)).findByName(cosmeticName);
        }
    }

    @Nested
    @DisplayName("Alternative & Exception Flow Tests")
    class ExceptionFlowTests {

        @Test
        @DisplayName("Should throw CosmeticNotFoundException when repository returns empty optional")
        void execute_WhenCosmeticDoesNotExist_ShouldThrowCosmeticNotFoundException() {
            // Arrange
            String cosmeticName = "Non Existent Skin";
            FindCosmeticByNameInput input = new FindCosmeticByNameInput(cosmeticName);

            when(cosmeticRepository.findByName(cosmeticName))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    CosmeticNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(cosmeticRepository, times(1)).findByName(cosmeticName);
        }

        @Test
        @DisplayName("Should propagate exception when repository throws an unexpected runtime exception")
        void execute_WhenRepositoryFails_ShouldPropagateException() {
            // Arrange
            String cosmeticName = "Golden Skin";
            FindCosmeticByNameInput input = new FindCosmeticByNameInput(cosmeticName);

            when(cosmeticRepository.findByName(cosmeticName))
                    .thenThrow(new RuntimeException("Database connection failure"));

            // Act & Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Database connection failure", exception.getMessage());
            verify(cosmeticRepository, times(1)).findByName(cosmeticName);
        }
    }

    @Nested
    @DisplayName("Edge Cases & Null/Empty Input Handling")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should throw NullPointerException when input is null")
        void execute_WhenInputIsNull_ShouldThrowNullPointerException() {
            // Act & Assert
            assertThrows(
                    NullPointerException.class,
                    () -> useCase.execute(null),
                    "UseCase should fail immediately with NPE when receiving null input"
            );

            verify(cosmeticRepository, never()).findByName(any());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should delegate to repository even when name in input is null, empty or blank")
        void execute_WhenNameIsBlankOrNull_ShouldDelegateToRepository(String blankOrNullName) {
            // Arrange
            FindCosmeticByNameInput input = new FindCosmeticByNameInput(blankOrNullName);

            when(cosmeticRepository.findByName(blankOrNullName))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    CosmeticNotFoundException.class,
                    () -> useCase.execute(input)
            );

            verify(cosmeticRepository, times(1)).findByName(blankOrNullName);
        }
    }
}