package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.game.usecase.SwapRoomPositionUseCase;
import com.letraaletra.api.presentation.dto.request.websocket.SwapPlaceWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SwapRoomPlaceHandler implements RoomRequestHandler<SwapPlaceWsRequest> {
    @Autowired
    private SwapRoomPositionUseCase swapRoomPosition;

    @Override
    public void handle(SwapPlaceWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        swapRoomPosition.execute(
                request.tokenGameId(),
                request.position(),
                userId
        );
    }

    @Override
    public Class<SwapPlaceWsRequest> getType() {
        return SwapPlaceWsRequest.class;
    }
}
