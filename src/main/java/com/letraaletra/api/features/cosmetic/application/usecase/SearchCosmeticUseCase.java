package com.letraaletra.api.features.cosmetic.application.usecase;

import com.letraaletra.api.features.cosmetic.application.input.SearchCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.SearchCosmeticOutput;
import com.letraaletra.api.features.cosmetic.domain.CosmeticsPage;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class SearchCosmeticUseCase implements UseCase<SearchCosmeticInput, SearchCosmeticOutput> {
    private final CosmeticRepository cosmeticRepository;

    public SearchCosmeticUseCase(
            CosmeticRepository cosmeticRepository
    ) {
        this.cosmeticRepository = cosmeticRepository;
    }

    @Override
    public SearchCosmeticOutput execute(SearchCosmeticInput input) {
        return new SearchCosmeticOutput(
                cosmeticRepository.search(
                        input.search(),
                        new CosmeticsPage(input.page(), input.size(), input.sort())
                )
        );
    }
}
