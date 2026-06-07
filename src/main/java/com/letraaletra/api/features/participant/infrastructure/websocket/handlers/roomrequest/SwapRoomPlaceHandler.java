package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.features.participant.application.usecase.SwapRoomPositionUseCase;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.SwapPositionWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.SwapPositionResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.SwapPositionMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SwapRoomPlaceHandler implements RoomRequestHandler<SwapPositionWsRequest> {
    private final SwapRoomPositionUseCase swapRoomPosition;
    private final GameNotifier gameNotifier;

    private SwapRoomPlaceHandler(
            SwapRoomPositionUseCase swapRoomPosition,
            GameNotifier gameNotifier
    ) {
        this.swapRoomPosition = swapRoomPosition;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(SwapPositionWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        SwapPositionInput command = SwapPositionMapper.toInput(request, userId);

        SwapPositionOutput output = swapRoomPosition.execute(command);

        SwapPositionResponse dto = SwapPositionMapper.toResponse(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<SwapPositionWsRequest> getType() {
        return SwapPositionWsRequest.class;
    }
}
