package com.letraaletra.api.features.cosmetic.infrastructure.loader;

import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CosmeticDatabaseSeeder implements CommandLineRunner {
    private final CosmeticLoader loader;
    private final CosmeticRepository repository;

    public CosmeticDatabaseSeeder(CosmeticLoader cosmeticLoader, CosmeticRepository cosmeticRepository) {
        this.loader = cosmeticLoader;
        this.repository = cosmeticRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loader.load().forEach(repository::save);
    }
}
