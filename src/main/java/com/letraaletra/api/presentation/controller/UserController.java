package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.application.user.usecase.FindUserByIdUseCase;
import com.letraaletra.api.application.user.usecase.RegisterUserUseCase;
import com.letraaletra.api.presentation.dto.request.user.CreateUserRequestDTO;
import com.letraaletra.api.presentation.dto.request.user.LoginRequestDTO;
import com.letraaletra.api.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.presentation.dto.response.user.LoginResponseDTO;
import com.letraaletra.api.presentation.dto.response.user.UserDTO;
import com.letraaletra.api.domain.user.UserMessages;
import com.letraaletra.api.application.user.usecase.AuthUseCase;
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
    private RegisterUserUseCase registerUser;

    @Autowired
    private FindUserByIdUseCase findUserById;

    @Autowired
    private AuthUseCase authUseCase;

    @PostMapping
    public ResponseEntity<SuccessResponse<UserDTO>> register(@Valid @RequestBody CreateUserRequestDTO request) {
        UserDTO result = registerUser.execute(
                request.nickname(),
                request.email(),
                request.password()
        );

        return ApiResponse.success(result, UserMessages.USER_CREATED.getMessage(), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<UserDTO>> get(@PathVariable @NotBlank String userId) {
        UserDTO result = findUserById.execute(userId);

        return ApiResponse.success(result, UserMessages.USER_FOUND.getMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO result = authUseCase.login(request);

        return ApiResponse.success(result, UserMessages.USER_LOGGED.getMessage());
    }
}
