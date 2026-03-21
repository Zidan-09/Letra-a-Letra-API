package com.letraaletra.api.controller;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.dto.request.game.CreateGameRequest;
import com.letraaletra.api.dto.response.SuccessResponse;
import com.letraaletra.api.exception.messages.GameMessages;
import com.letraaletra.api.service.GameService;
import com.letraaletra.api.utils.server.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/game")
public class GameController {

}
