package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.CreateUserInput;
import com.letraaletra.api.features.user.application.output.CreateUserOutput;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.CreateUserRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.CreateUserResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.CreateUserMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class CreateUserController {
    private final UseCase<CreateUserInput, CreateUserOutput> createUserUseCase;

    public CreateUserController(UseCase<CreateUserInput, CreateUserOutput> createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<CreateUserResponse>> handle(
            @Valid @RequestBody CreateUserRequest request
    ) {
        CreateUserInput input = CreateUserMapper.toInput(request);

        CreateUserOutput output = createUserUseCase.execute(input);

        CreateUserResponse dto = CreateUserMapper.toResponse(output);

        return ApiResponseService.success(dto, HttpStatus.CREATED);
    }
}
