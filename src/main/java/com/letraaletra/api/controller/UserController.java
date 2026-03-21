package com.letraaletra.api.controller;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.dto.response.SuccessResponse;
import com.letraaletra.api.service.UserService;
import com.letraaletra.api.utils.server.ApiResponse;
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
    public ResponseEntity<SuccessResponse<User>> create(@RequestBody User body) {
        User result = userService.create(body);

        return ApiResponse.success(result, "deu", HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<User>> get(@PathVariable String userId) {


        return ApiResponse.success(null);
    }
}
