package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.CreateUserInput;
import com.letraaletra.api.features.user.application.output.CreateUserOutput;
import com.letraaletra.api.features.user.application.usecase.CreateUserUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.presentation.controller.ApiResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.CreateUserRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.CreateUserResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.CreateUserMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class CreateUserController {
    private final CreateUserUseCase createUserUseCase;

    public CreateUserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<CreateUserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserInput input = CreateUserMapper.toInput(request);

        CreateUserOutput output = createUserUseCase.execute(input);

        CreateUserResponse dto = CreateUserMapper.toResponse(output);

        return ApiResponse.success(dto, UserMessages.USER_CREATED.getMessage(), HttpStatus.CREATED);
    }
}
