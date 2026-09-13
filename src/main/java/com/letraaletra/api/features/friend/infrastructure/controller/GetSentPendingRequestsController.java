package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.GetSentPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetSentPendingRequestsOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetSentPendingRequestsResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetSentPendingRequestsMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/friend")
@Tag(name = "Friend", description = "Rotas relacionadas a funcionalidade de amizades")
public class GetSentPendingRequestsController {
    private final UseCase<GetSentPendingRequestsInput, GetSentPendingRequestsOutput> useCase;

    @GetMapping("/pending/sent")
    public ResponseEntity<SuccessResponse<GetSentPendingRequestsResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        GetSentPendingRequestsInput input = GetSentPendingRequestsMapper.toInput(principal.auth());

        GetSentPendingRequestsOutput output = useCase.execute(input);

        GetSentPendingRequestsResponse dto = GetSentPendingRequestsMapper.toResponse(output, principal.auth());

        return ApiResponseHandler.success(dto);
    }
}
