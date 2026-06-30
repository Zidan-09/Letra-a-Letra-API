package com.letraaletra.api.features.cosmetic.domain.repository;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GetCosmetics {
    Page<Cosmetic> get(GetCosmeticsInput input);
}
