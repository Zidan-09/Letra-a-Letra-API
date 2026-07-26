package com.letraaletra.api.features.cosmetic.domain.repository;

import com.letraaletra.api.features.cosmetic.domain.CosmeticsPage;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import org.springframework.data.domain.Page;

public interface GetCosmetics {
    Page<Cosmetic> get(CosmeticsPage page);
}
