package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.LeftGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.LeftGameMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.assembler.GameResponseAssembler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class LeftGameHandler implements RoomRequestHandler<LeftGameWsRequest> {
    private final UseCase<LeftGameInput, LeftGameOutput> useCase;
    private final GameResponseAssembler gameResponseAssembler;
    private final GameNotifier gameNotifier;

    @Transactional
    @Override
    public void handle(LeftGameWsRequest request, WebSocketSession session) {
        LeftGameInput input = LeftGameMapper.toInput(request, session.getId());

        LeftGameOutput output = useCase.execute(input);

        LeftGameResponse dto = LeftGameMapper.toResponse(output);

        gameNotifier.notifierAll(output.game(), dto);

        output.gameOver().ifPresent(gameOver -> {
            WsResponse gameOverDto = gameResponseAssembler.assembleGameOver(output.game(), gameOver);

            gameNotifier.notifierGameOver(output.game(), gameOverDto);
        });
    }

    @Override
    public Class<LeftGameWsRequest> getType() {
        return LeftGameWsRequest.class;
    }
}
