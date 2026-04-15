package com.letraaletra.api.application.port;

public interface ActorRepository {
    Actor getOrCreate(String id);
    void remove(String id);
}
