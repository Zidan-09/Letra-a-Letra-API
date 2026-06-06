package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.UpdateNicknameInput;
import com.letraaletra.api.features.user.application.output.UpdateNicknameOutput;
import com.letraaletra.api.features.user.application.usecase.UpdateNicknameUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.UpdateNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.UpdateNicknameResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.SetNicknameMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UpdateNicknameController {
    private final UpdateNicknameUseCase updateNicknameUseCase;

    public UpdateNicknameController(UpdateNicknameUseCase updateNicknameUseCase) {
        this.updateNicknameUseCase = updateNicknameUseCase;
    }

    @PatchMapping("/nickname/{userId}")
    public ResponseEntity<SuccessResponse<UpdateNicknameResponse>> updateNickname(@Valid @RequestBody UpdateNicknameRequest request, @PathVariable @NotBlank String userId) {
        UpdateNicknameInput input = SetNicknameMapper.toInput(request, userId);

        UpdateNicknameOutput output = updateNicknameUseCase.execute(input);

        UpdateNicknameResponse dto = SetNicknameMapper.toResponse(output);

        return ApiResponseService.success(dto, UserMessages.NICKNAME_SETTER.getMessage());
    }
}
