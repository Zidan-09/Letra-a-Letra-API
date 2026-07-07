package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.application.usecase.ChangeNicknameUseCase;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ChangeNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeNicknameResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.ChangeNicknameMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class ChangeNicknameController {
    private final ChangeNicknameUseCase changeNicknameUseCase;

    public ChangeNicknameController(ChangeNicknameUseCase changeNicknameUseCase) {
        this.changeNicknameUseCase = changeNicknameUseCase;
    }

    @PatchMapping("/nickname/{userId}")
    public ResponseEntity<SuccessResponse<ChangeNicknameResponse>> updateNickname(@Valid @RequestBody ChangeNicknameRequest request, @PathVariable @NotBlank String userId) {
        ChangeNicknameInput input = ChangeNicknameMapper.toInput(request, userId);

        ChangeNicknameOutput output = changeNicknameUseCase.execute(input);

        ChangeNicknameResponse dto = ChangeNicknameMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
