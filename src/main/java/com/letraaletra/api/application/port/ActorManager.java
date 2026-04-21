package com.letraaletra.api.application.port;

import java.util.List;

public interface ActorManager {
    void create(String id);
    Actor get(String id);
    List<Actor> getAllActors();
    void remove(String id);
}
