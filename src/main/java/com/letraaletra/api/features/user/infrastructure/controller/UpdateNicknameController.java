package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.SetNicknameInput;
import com.letraaletra.api.features.user.application.output.SetNicknameOutput;
import com.letraaletra.api.features.user.application.usecase.SetNicknameUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.UpdateNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.SetNicknameResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.SetNicknameMapper;
import com.letraaletra.api.presentation.controller.ApiResponse;
import com.letraaletra.api.presentation.dto.response.http.SuccessResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UpdateNicknameController {
    private final SetNicknameUseCase setNicknameUseCase;
    private final SetNicknameMapper setNicknameMapper;

    public UpdateNicknameController(SetNicknameUseCase setNicknameUseCase, SetNicknameMapper setNicknameMapper) {
        this.setNicknameUseCase = setNicknameUseCase;
        this.setNicknameMapper = setNicknameMapper;
    }

    @PatchMapping("/nickname/{userId}")
    public ResponseEntity<SuccessResponseDTO<SetNicknameResponse>> updateNickname(@Valid @RequestBody UpdateNicknameRequest request, @PathVariable @NotBlank String userId) {
        SetNicknameInput input = setNicknameMapper.toInput(request, userId);

        SetNicknameOutput output = setNicknameUseCase.execute(input);

        SetNicknameResponse dto = setNicknameMapper.toResponse(output);

        return ApiResponse.success(dto, UserMessages.NICKNAME_SETTER.getMessage());
    }
}
