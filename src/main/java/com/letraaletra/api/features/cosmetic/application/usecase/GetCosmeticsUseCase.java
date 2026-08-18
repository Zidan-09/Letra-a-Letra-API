package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.GetCosmeticsInput;
import com.letraaletra.api.features.cosmetic.application.output.GetCosmeticsOutput;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticsPage;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetCosmeticsUseCase implements UseCase<GetCosmeticsInput, GetCosmeticsOutput> {
    private final CosmeticRepository cosmeticRepository;

    public GetCosmeticsUseCase(
            CosmeticRepository cosmeticRepository
    ) {
        this.cosmeticRepository = cosmeticRepository;
    }

    @Override
    public GetCosmeticsOutput execute(GetCosmeticsInput input) {
        Page<Cosmetic> cosmetics = cosmeticRepository.get(
                new CosmeticsPage(
                        input.page(),
                        input.size(),
                        input.sort()
                )
        );

        return new GetCosmeticsOutput(cosmetics);
    }
}
