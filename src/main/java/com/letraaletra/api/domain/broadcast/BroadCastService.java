package com.letraaletra.api.domain.broadcast;

import com.letraaletra.api.presentation.dto.response.websocket.WsResponseDTO;

public interface BroadCastService {
    void send(String gameId, WsResponseDTO dto);
}
