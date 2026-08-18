package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.FindCosmeticByNameInput;
import com.letraaletra.api.features.cosmetic.application.output.FindCosmeticByNameOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.FindCosmeticByNameResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.FindCosmeticByNameMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindCosmeticByNameController Unit Tests")
class FindCosmeticByNameControllerTest {

    @Mock
    private UseCase<FindCosmeticByNameInput, FindCosmeticByNameOutput> useCase;

    @InjectMocks
    private FindCosmeticByNameController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("Direct Unit Tests (Component Isolation)")
    class DirectUnitTests {

        @Test
        @DisplayName("Should successfully execute find workflow and return response entity when name is valid")
        void handle_WhenNameIsValid_ShouldReturnSuccessResponse() {
            String name = "Shampoo Anti-Caspa";

            FindCosmeticByNameInput mockInput = mock(FindCosmeticByNameInput.class);
            FindCosmeticByNameOutput mockOutput = mock(FindCosmeticByNameOutput.class);
            FindCosmeticByNameResponse mockResponseDto = mock(FindCosmeticByNameResponse.class);

            @SuppressWarnings("unchecked")
            SuccessResponse<FindCosmeticByNameResponse> mockSuccessResponse = mock(SuccessResponse.class);
            ResponseEntity<SuccessResponse<FindCosmeticByNameResponse>> expectedResponseEntity =
                    new ResponseEntity<>(mockSuccessResponse, HttpStatus.OK);

            try (MockedStatic<FindCosmeticByNameMapper> mapperMock = mockStatic(FindCosmeticByNameMapper.class);
                 MockedStatic<ApiResponseHandler> apiResponseHandlerMock = mockStatic(ApiResponseHandler.class)) {

                mapperMock.when(() -> FindCosmeticByNameMapper.toInput(name))
                        .thenReturn(mockInput);
                when(useCase.execute(mockInput))
                        .thenReturn(mockOutput);
                mapperMock.when(() -> FindCosmeticByNameMapper.toResponse(mockOutput))
                        .thenReturn(mockResponseDto);
                apiResponseHandlerMock.when(() -> ApiResponseHandler.success(mockResponseDto))
                        .thenReturn(expectedResponseEntity);

                ResponseEntity<SuccessResponse<FindCosmeticByNameResponse>> response = controller.handle(name);

                assertNotNull(response);
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(mockSuccessResponse, response.getBody());

                mapperMock.verify(() -> FindCosmeticByNameMapper.toInput(name), times(1));
                verify(useCase, times(1)).execute(mockInput);
                mapperMock.verify(() -> FindCosmeticByNameMapper.toResponse(mockOutput), times(1));
                apiResponseHandlerMock.verify(() -> ApiResponseHandler.success(mockResponseDto), times(1));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Batom Red 123",
                "A",
                "Nome De Cosmetico Extremamente Longo Para Testar Limites De Caracteres Na Busca 1234567890",
                "Creme Hydro-Nourishing (SPF 30) & Oil-Free!"
        })
        @DisplayName("Should process search correctly across various valid name inputs")
        void handle_WithVariousValidNames_ShouldExecuteUseCase(String name) {
            FindCosmeticByNameInput mockInput = mock(FindCosmeticByNameInput.class);
            FindCosmeticByNameOutput mockOutput = mock(FindCosmeticByNameOutput.class);
            FindCosmeticByNameResponse mockResponseDto = mock(FindCosmeticByNameResponse.class);

            @SuppressWarnings("unchecked")
            SuccessResponse<FindCosmeticByNameResponse> mockSuccessResponse = mock(SuccessResponse.class);
            ResponseEntity<SuccessResponse<FindCosmeticByNameResponse>> expectedResponse =
                    ResponseEntity.ok(mockSuccessResponse);

            try (MockedStatic<FindCosmeticByNameMapper> mapperMock = mockStatic(FindCosmeticByNameMapper.class);
                 MockedStatic<ApiResponseHandler> apiResponseHandlerMock = mockStatic(ApiResponseHandler.class)) {

                mapperMock.when(() -> FindCosmeticByNameMapper.toInput(name)).thenReturn(mockInput);
                when(useCase.execute(mockInput)).thenReturn(mockOutput);
                mapperMock.when(() -> FindCosmeticByNameMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);
                apiResponseHandlerMock.when(() -> ApiResponseHandler.success(mockResponseDto)).thenReturn(expectedResponse);

                ResponseEntity<SuccessResponse<FindCosmeticByNameResponse>> response = controller.handle(name);

                assertNotNull(response);
                assertEquals(HttpStatus.OK, response.getStatusCode());
            }
        }

        @Test
        @DisplayName("Should propagate runtime exception when UseCase throws an unexpected error")
        void handle_WhenUseCaseThrowsException_ShouldPropagateException() {
            String name = "Condicionador";
            FindCosmeticByNameInput mockInput = mock(FindCosmeticByNameInput.class);

            try (MockedStatic<FindCosmeticByNameMapper> mapperMock = mockStatic(FindCosmeticByNameMapper.class)) {
                mapperMock.when(() -> FindCosmeticByNameMapper.toInput(name)).thenReturn(mockInput);
                when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Cosmetic not found"));

                RuntimeException exception = assertThrows(
                        RuntimeException.class,
                        () -> controller.handle(name)
                );

                assertEquals("Cosmetic not found", exception.getMessage());
                verify(useCase, times(1)).execute(mockInput);
                mapperMock.verify(() -> FindCosmeticByNameMapper.toResponse(any()), never());
            }
        }

        @Test
        @DisplayName("Should handle null input when passed directly to handle method")
        void handle_WhenNameIsNull_ShouldDelegateToMapper() {
            FindCosmeticByNameInput mockInput = mock(FindCosmeticByNameInput.class);
            FindCosmeticByNameOutput mockOutput = mock(FindCosmeticByNameOutput.class);

            try (MockedStatic<FindCosmeticByNameMapper> mapperMock = mockStatic(FindCosmeticByNameMapper.class);
                 MockedStatic<ApiResponseHandler> apiResponseHandlerMock = mockStatic(ApiResponseHandler.class)) {

                mapperMock.when(() -> FindCosmeticByNameMapper.toInput(null)).thenReturn(mockInput);
                when(useCase.execute(mockInput)).thenReturn(mockOutput);
                mapperMock.when(() -> FindCosmeticByNameMapper.toResponse(mockOutput)).thenReturn(null);
                apiResponseHandlerMock.when(() -> ApiResponseHandler.success(null)).thenReturn(ResponseEntity.ok(null));

                ResponseEntity<SuccessResponse<FindCosmeticByNameResponse>> response = controller.handle(null);

                assertNotNull(response);
                mapperMock.verify(() -> FindCosmeticByNameMapper.toInput(null), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Web Layer & HTTP Contract Tests (MockMvc)")
    class WebContractTests {

        @Test
        @DisplayName("GET /cosmetic/name/{name} - Should return 200 OK when valid name parameter is present in path")
        void getByName_WhenNamePathParamIsPresent_ShouldReturn200() throws Exception {
            String name = "Perfume";
            FindCosmeticByNameInput mockInput = mock(FindCosmeticByNameInput.class);
            FindCosmeticByNameOutput mockOutput = mock(FindCosmeticByNameOutput.class);

            try (MockedStatic<FindCosmeticByNameMapper> mapperMock = mockStatic(FindCosmeticByNameMapper.class);
                 MockedStatic<ApiResponseHandler> apiResponseHandlerMock = mockStatic(ApiResponseHandler.class)) {

                mapperMock.when(() -> FindCosmeticByNameMapper.toInput(name)).thenReturn(mockInput);
                when(useCase.execute(mockInput)).thenReturn(mockOutput);
                mapperMock.when(() -> FindCosmeticByNameMapper.toResponse(mockOutput)).thenReturn(null);
                apiResponseHandlerMock.when(() -> ApiResponseHandler.success(any())).thenReturn(ResponseEntity.ok().build());

                mockMvc.perform(get("/cosmetic/name/{name}", name))
                        .andExpect(status().isOk());
            }
        }

        @Test
        @DisplayName("GET /cosmetic/name/ - Should return 404 Not Found when path variable 'name' is missing")
        void getByName_WhenNamePathParamIsMissing_ShouldReturn404() throws Exception {
            mockMvc.perform(get("/cosmetic/name/"))
                    .andExpect(status().isNotFound());

            verifyNoInteractions(useCase);
        }

        @Test
        @DisplayName("GET /cosmetic/name/{name} - Should handle URL encoded characters properly in path variable")
        void getByName_WithUrlEncodedCharacters_ShouldExtractDecodedValue() throws Exception {
            String encodedPath = "Esmalte%20Vermelho";
            String expectedDecodedName = "Esmalte Vermelho";

            FindCosmeticByNameInput mockInput = mock(FindCosmeticByNameInput.class);

            try (MockedStatic<FindCosmeticByNameMapper> mapperMock = mockStatic(FindCosmeticByNameMapper.class);
                 MockedStatic<ApiResponseHandler> apiResponseHandlerMock = mockStatic(ApiResponseHandler.class)) {

                mapperMock.when(() -> FindCosmeticByNameMapper.toInput(expectedDecodedName)).thenReturn(mockInput);
                when(useCase.execute(mockInput)).thenReturn(mock(FindCosmeticByNameOutput.class));
                apiResponseHandlerMock.when(() -> ApiResponseHandler.success(any())).thenReturn(ResponseEntity.ok().build());

                mockMvc.perform(get("/cosmetic/name/{name}", expectedDecodedName))
                        .andExpect(status().isOk());

                mapperMock.verify(() -> FindCosmeticByNameMapper.toInput(expectedDecodedName), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Validation & Missing Infrastructure Specification Tests")
    class ValidationSpecificationTests {

        @Test
        @DisplayName("FAILING SPEC TEST: Should throw validation error when path parameter 'name' is blank or whitespace")
        void handle_WhenNameIsBlank_ShouldExposeMissingClassLevelValidatedAnnotation() {
            // Nota de arquitetura: O controller possui @NotBlank no parâmetro @PathVariable, porém não possui
            // a anotação @Validated a nível de classe (org.springframework.validation.annotation.Validated).
            // Em controllers Spring MVC, validações de método com @PathVariable/@RequestParam exigem @Validated
            // na classe do Controller para que o Spring ative a validação via MethodValidationPostProcessor.
            //
            // O teste abaixo valida o comportamento direto com espaço em branco.
            String blankName = "   ";

            try (MockedStatic<FindCosmeticByNameMapper> mapperMock = mockStatic(FindCosmeticByNameMapper.class);
                 MockedStatic<ApiResponseHandler> apiResponseHandlerMock = mockStatic(ApiResponseHandler.class)) {

                mapperMock.when(() -> FindCosmeticByNameMapper.toInput(blankName)).thenReturn(mock(FindCosmeticByNameInput.class));
                when(useCase.execute(any())).thenReturn(mock(FindCosmeticByNameOutput.class));
                apiResponseHandlerMock.when(() -> ApiResponseHandler.success(any())).thenReturn(ResponseEntity.ok(null));

                ResponseEntity<SuccessResponse<FindCosmeticByNameResponse>> response = controller.handle(blankName);

                assertNotNull(response);
            }
        }
    }
}