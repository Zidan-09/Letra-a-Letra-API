package com.letraaletra.api.controller;

import com.letraaletra.api.dto.request.user.CreateUserRequestDTO;
import com.letraaletra.api.dto.response.SuccessResponse;
import com.letraaletra.api.dto.response.user.UserDTO;
import com.letraaletra.api.exception.messages.UserMessages;
import com.letraaletra.api.service.UserService;
import com.letraaletra.api.utils.server.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<SuccessResponse<UserDTO>> create(@RequestBody @Valid CreateUserRequestDTO request) {
        UserDTO result = userService.create(
                request.nickname(),
                request.avatar(),
                request.email(),
                request.password()
        );

        return ApiResponse.success(result, UserMessages.USER_CREATED.getMessage(), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<UserDTO>> get(@PathVariable @NotBlank String userId) {
        UserDTO result = userService.find(userId);

        return ApiResponse.success(result, UserMessages.USER_FOUND.getMessage());
    }
}
