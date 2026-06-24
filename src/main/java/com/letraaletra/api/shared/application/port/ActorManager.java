package com.letraaletra.api.shared.application.port;

import java.util.List;
import java.util.UUID;

public interface ActorManager<T> {
    void create(UUID id, T actor);
    Actor get(UUID id);
    List<Actor> getAllActors();
    void remove(UUID id);
}
