package com.letraaletra.api.features.user.application.service;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class UnlockCosmeticService {
    private final UserRepository userRepository;
    private final CosmeticRepository cosmeticRepository;

    public UnlockCosmeticService(UserRepository userRepository, CosmeticRepository cosmeticRepository) {
        this.userRepository = userRepository;
        this.cosmeticRepository = cosmeticRepository;
    }

    public void execute(String cosmeticId, UUID userId) {
        Cosmetic cosmetic = cosmeticRepository.find(cosmeticId)
                .orElseThrow(CosmeticNotFoundException::new);

        User user = userRepository.find(userId)
                .orElseThrow(UserNotFoundException::new);

        InventoryItem item = new InventoryItem(
            cosmeticId,
            cosmetic.getName(),
            cosmetic.getType(),
            false,
            LocalDateTime.now()
        );

        user.addToInventory(item);

        userRepository.save(user);
    }
}
