package com.letraaletra.api.features.cosmetic.application.input;

public record GetCosmeticsInput(
        int page,
        int size,
        String sort
) {
}
