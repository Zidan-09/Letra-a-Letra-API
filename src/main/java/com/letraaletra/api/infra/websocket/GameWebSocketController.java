package com.letraaletra.api.infra.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {
    @MessageMapping("/game/move")
    public void handleMove() {

    }
}
