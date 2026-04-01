package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.application.command.user.CreateUserCommand;
import com.letraaletra.api.application.command.user.FindUserCommand;
import com.letraaletra.api.application.command.user.SignInCommand;
import com.letraaletra.api.application.output.user.CreateUserOutput;
import com.letraaletra.api.application.output.user.FindUserOutput;
import com.letraaletra.api.application.output.user.SignInOutput;
import com.letraaletra.api.application.usecase.user.FindUserByIdUseCase;
import com.letraaletra.api.application.usecase.user.CreateUserUseCase;
import com.letraaletra.api.presentation.dto.request.user.CreateUserRequestDTO;
import com.letraaletra.api.presentation.dto.request.user.SignInRequestDTO;
import com.letraaletra.api.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.presentation.dto.response.user.CreateUserResponseDTO;
import com.letraaletra.api.presentation.dto.response.user.FindUserResponseDTO;
import com.letraaletra.api.presentation.dto.response.user.SignInResponseDTO;
import com.letraaletra.api.domain.user.UserMessages;
import com.letraaletra.api.application.usecase.user.AuthUseCase;
import com.letraaletra.api.presentation.mappers.user.CreateUserMapper;
import com.letraaletra.api.presentation.mappers.user.FindUserMapper;
import com.letraaletra.api.presentation.mappers.user.SignInMapper;
import com.letraaletra.api.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
@Validated
public class UserController {

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private CreateUserMapper createUserMapper;

    @Autowired
    private SignInMapper signInMapper;

    @Autowired
    private FindUserMapper findUserMapper;

    @Autowired
    private FindUserByIdUseCase findUserById;

    @Autowired
    private AuthUseCase authUseCase;

    @PostMapping
    public ResponseEntity<SuccessResponse<CreateUserResponseDTO>> register(@Valid @RequestBody CreateUserRequestDTO request) {
        CreateUserCommand command = createUserMapper.toCommand(request);

        CreateUserOutput output = createUserUseCase.execute(command);

        CreateUserResponseDTO dto = createUserMapper.toResponseDTO(output);

        return ApiResponse.success(dto, UserMessages.USER_CREATED.getMessage(), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<FindUserResponseDTO>> find(@PathVariable @NotBlank String userId) {
        FindUserCommand command = findUserMapper.toCommand(userId);

        FindUserOutput output = findUserById.execute(command);

        FindUserResponseDTO dto = findUserMapper.toResponseDTO(output);

        return ApiResponse.success(dto, UserMessages.USER_FOUND.getMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<SignInResponseDTO>> login(@Valid @RequestBody SignInRequestDTO request) {
        SignInCommand command = signInMapper.toCommand(request);

        SignInOutput output = authUseCase.login(command);

        SignInResponseDTO dto = signInMapper.toResponseDTO(output);

        return ApiResponse.success(dto, UserMessages.USER_LOGGED.getMessage());
    }
}
