package com.letraaletra.api.features.offers.application.usecase;

import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.application.output.GetOffersOutput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetOffersUseCase implements UseCase<GetOffersInput, GetOffersOutput> {
    private final OfferRepository offerRepository;

    public GetOffersUseCase(
            OfferRepository offerRepository
    ) {
        this.offerRepository = offerRepository;
    }

    @Override
    public GetOffersOutput execute(GetOffersInput input) {
        Page<Offer> offers = offerRepository.get(input);

        return new GetOffersOutput(offers);
    }
}
