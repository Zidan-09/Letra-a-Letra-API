package com.letraaletra.api.application.port;

public interface ActorManager {
    Actor getOrCreate(String id);
    void remove(String id);
}
