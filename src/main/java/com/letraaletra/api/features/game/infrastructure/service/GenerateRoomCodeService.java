package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.application.port.RoomCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class GenerateRoomCodeService implements RoomCodeService {
    private final SecureRandom random = new SecureRandom();
    private final GameQueryService gameQueryService;

    @Override
    public String generate() {
        String code;

        do {
            code = getCode();
        } while (gameQueryService.existsByCode(code));

        return code;
    }

    private String getCode() {
        int length = 6;
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            String base = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
            int index = random.nextInt(base.length());
            code.append(base.charAt(index));
        }

        return code.toString();
    }
}
