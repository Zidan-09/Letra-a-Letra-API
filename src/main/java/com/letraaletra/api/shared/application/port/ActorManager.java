package com.letraaletra.api.shared.application.port;

import java.util.List;

public interface ActorManager<T> {
    void create(String id, T actor);
    Actor get(String id);
    List<Actor> getAllActors();
    void remove(String id);
}
