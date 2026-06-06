package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.ChangeCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.user.SetAvatarResponseDTO;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.SetAvatarMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.user.SetAvatarRequestDTO;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
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
    private ChangeCosmeticUseCase changeCosmeticUseCase;


    @PatchMapping("/avatar/{userId}")
    public ResponseEntity<SuccessResponse<SetAvatarResponseDTO>> setAvatar(@Valid @RequestBody SetAvatarRequestDTO request, @PathVariable @NotBlank String userId) {
        ChangeCosmeticInput command = setAvatarMapper.toCommand(request, userId);

        ChangeCosmeticOutput output = changeCosmeticUseCase.execute(command);

        SetAvatarResponseDTO dto = setAvatarMapper.toResponseDTO(output);

        return ApiResponseService.success(dto, UserMessages.AVATAR_SETTER.getMessage());
    }
}
