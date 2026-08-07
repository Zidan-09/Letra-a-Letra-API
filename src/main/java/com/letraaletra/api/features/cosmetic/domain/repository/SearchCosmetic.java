package com.letraaletra.api.features.cosmetic.domain.repository;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticsPage;
import org.springframework.data.domain.Page;

public interface SearchCosmetic {
    Page<Cosmetic> search(String search, CosmeticsPage page);
}
