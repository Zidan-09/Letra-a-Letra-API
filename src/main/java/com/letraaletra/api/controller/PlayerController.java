package com.letraaletra.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/player")
public class PlayerController {

    @GetMapping
    public ResponseEntity<String> get(String id) {
        return ResponseEntity.ok("Player Abacate Encontrado!");
    }
}
