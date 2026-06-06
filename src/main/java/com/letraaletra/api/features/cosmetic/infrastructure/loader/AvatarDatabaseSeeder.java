package com.letraaletra.api.features.cosmetic.infrastructure.loader;

import com.letraaletra.api.features.cosmetic.domain.repository.AvatarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AvatarDatabaseSeeder implements CommandLineRunner {
    private final AvatarLoader loader;
    private final AvatarRepository repository;

    public AvatarDatabaseSeeder(AvatarLoader avatarLoader, AvatarRepository avatarRepository) {
        this.loader = avatarLoader;
        this.repository = avatarRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loader.load().values().forEach(repository::save);
    }
}
