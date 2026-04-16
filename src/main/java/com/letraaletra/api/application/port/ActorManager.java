package com.letraaletra.api.application.port;

import java.util.List;

public interface ActorManager {
    Actor getOrCreate(String id);
    List<Actor> getAllActors();
    void remove(String id);
}
