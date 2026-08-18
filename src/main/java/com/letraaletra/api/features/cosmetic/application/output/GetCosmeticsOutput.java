package com.letraaletra.api.features.cosmetic.application.output;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import org.springframework.data.domain.Page;

public record GetCosmeticsOutput(
        Page<Cosmetic> cosmetics
) {
}
