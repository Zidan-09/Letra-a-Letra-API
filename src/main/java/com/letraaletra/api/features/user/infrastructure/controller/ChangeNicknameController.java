package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.application.usecase.ChangeNicknameUseCase;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ChangeNicknameRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeNicknameResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.ChangeNicknameMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class ChangeNicknameController {
    private final ChangeNicknameUseCase changeNicknameUseCase;

    public ChangeNicknameController(ChangeNicknameUseCase changeNicknameUseCase) {
        this.changeNicknameUseCase = changeNicknameUseCase;
    }

    @PatchMapping("/nickname")
    public ResponseEntity<SuccessResponse<ChangeNicknameResponse>> updateNickname(
            @AuthenticationPrincipal UUID auth,
            @Valid @RequestBody ChangeNicknameRequest request
    ) {
        ChangeNicknameInput input = ChangeNicknameMapper.toInput(auth, request);

        ChangeNicknameOutput output = changeNicknameUseCase.execute(input);

        ChangeNicknameResponse dto = ChangeNicknameMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
