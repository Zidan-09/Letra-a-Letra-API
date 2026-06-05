package com.letraaletra.api.shared.infrastructure.concurrency;

import java.util.List;

public interface ActorManager<T> {
    void create(String id, T ator);
    Actor get(String id);
    List<Actor> getAllActors();
    void remove(String id);
}
