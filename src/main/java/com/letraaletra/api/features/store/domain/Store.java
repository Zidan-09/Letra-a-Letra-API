package com.letraaletra.api.features.store.domain;

import com.letraaletra.api.features.store.domain.offer.Offer;

import java.util.ArrayList;
import java.util.List;

public class Store {
    private List<Offer> offers;

    public Store() {
        this.offers = new ArrayList<Offer>();
    }

    public void addOffer(Offer offer) {
        offers.add(offer);
    }

    public List<Offer> getOffers() {
        return List.copyOf(offers);
    }
}
