package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.*;
import com.letraaletra.api.features.user.application.output.*;
import com.letraaletra.api.features.user.application.usecase.*;
import com.letraaletra.api.presentation.mappers.user.SetAvatarMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.presentation.dto.request.user.SetAvatarRequestDTO;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.presentation.dto.response.user.*;
import com.letraaletra.api.features.user.domain.UserMessages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
@Validated
public class UserController {

    @Autowired
    private SetAvatarMapper setAvatarMapper;

    @Autowired
    private SetAvatarUseCase setAvatarUseCase;


    @PatchMapping("/avatar/{userId}")
    public ResponseEntity<SuccessResponse<SetAvatarResponseDTO>> setAvatar(@Valid @RequestBody SetAvatarRequestDTO request, @PathVariable @NotBlank String userId) {
        SetAvatarInput command = setAvatarMapper.toCommand(request, userId);

        SetAvatarOutput output = setAvatarUseCase.execute(command);

        SetAvatarResponseDTO dto = setAvatarMapper.toResponseDTO(output);

        return ApiResponseService.success(dto, UserMessages.AVATAR_SETTER.getMessage());
    }
}
