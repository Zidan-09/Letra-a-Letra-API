package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetFriendListResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetFriendListMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetFriendListControllerTest {

    @Mock
    private UseCase<GetFriendListInput, GetFriendListOutput> useCase;

    @InjectMocks
    private GetFriendListController controller;

    private UUID mockAuthId;
    private GetFriendListInput mockInput;
    private GetFriendListOutput mockOutput;
    private GetFriendListResponse mockResponseDto;
    private SuccessResponse<GetFriendListResponse> mockSuccessResponse;

    @BeforeEach
    void setUp() {
        mockAuthId = UUID.randomUUID();
        mockInput = mock(GetFriendListInput.class);
        mockOutput = mock(GetFriendListOutput.class);
        mockResponseDto = mock(GetFriendListResponse.class);
        mockSuccessResponse = mock(SuccessResponse.class);
    }

    @Test
    @DisplayName("Deve retornar a lista de amigos com sucesso (200 OK) quando o usuário estiver autenticado")
    void getFriends_ShouldReturnFriendList_WhenUserIsAuthenticated() {
        try (MockedStatic<GetFriendListMapper> mapperMock = mockStatic(GetFriendListMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetFriendListMapper.toInput(mockAuthId.toString())).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetFriendListMapper.toResponse(mockOutput)).thenReturn(mockResponseDto);

            ResponseEntity<SuccessResponse<GetFriendListResponse>> expectedResponseEntity =
                    ResponseEntity.ok(mockSuccessResponse);
            apiResponseMock.when(() -> ApiResponseService.success(mockResponseDto)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetFriendListResponse>> response = controller.getFriends(mockAuthId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockSuccessResponse, response.getBody());

            verify(useCase).execute(mockInput);
        }
    }

    @Test
    @DisplayName("Deve propagar erro sem interceptação local caso o UseCase falhe")
    void getFriends_ShouldPropagateException_WhenUseCaseThrowsException() {
        try (MockedStatic<GetFriendListMapper> mapperMock = mockStatic(GetFriendListMapper.class)) {

            mapperMock.when(() -> GetFriendListMapper.toInput(mockAuthId.toString())).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenThrow(new RuntimeException("Database timeout or internal breakdown"));

            assertThrows(RuntimeException.class, () -> controller.getFriends(mockAuthId));

            mapperMock.verify(() -> GetFriendListMapper.toResponse(any()), never());
        }
    }

    @Test
    @DisplayName("Deve propagar a exceção original se o Mapper de entrada falhar")
    void getFriends_ShouldThrowException_WhenMapperToInputThrowsException() {
        try (MockedStatic<GetFriendListMapper> mapperMock = mockStatic(GetFriendListMapper.class)) {

            mapperMock.when(() -> GetFriendListMapper.toInput(anyString()))
                    .thenThrow(new IllegalArgumentException("Invalid string identity context"));

            assertThrows(IllegalArgumentException.class, () -> controller.getFriends(mockAuthId));

            verify(useCase, never()).execute(any());
        }
    }

    @Test
    @DisplayName("Deve garantir comportamento estrutural correto se o Mapper de resposta retornar nulo (Comportamento Desejado/Ausente)")
    void getFriends_ShouldHandleGracefully_WhenMapperToResponseReturnsNull() {
        try (MockedStatic<GetFriendListMapper> mapperMock = mockStatic(GetFriendListMapper.class);
             MockedStatic<ApiResponseService> apiResponseMock = mockStatic(ApiResponseService.class)) {

            mapperMock.when(() -> GetFriendListMapper.toInput(mockAuthId.toString())).thenReturn(mockInput);
            when(useCase.execute(mockInput)).thenReturn(mockOutput);
            mapperMock.when(() -> GetFriendListMapper.toResponse(mockOutput)).thenReturn(null);

            ResponseEntity<SuccessResponse<GetFriendListResponse>> expectedResponseEntity =
                    ResponseEntity.noContent().build();

            apiResponseMock.when(() -> ApiResponseService.success(null)).thenReturn(expectedResponseEntity);

            ResponseEntity<SuccessResponse<GetFriendListResponse>> response = controller.getFriends(mockAuthId);

            assertNotNull(response);
            verify(useCase).execute(mockInput);
        }
    }
}