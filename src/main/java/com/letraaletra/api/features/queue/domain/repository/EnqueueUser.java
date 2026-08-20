package com.letraaletra.api.features.queue.domain.repository;

import com.letraaletra.api.features.queue.domain.OnlineUser;
import com.letraaletra.api.features.queue.domain.QueueType;

public interface EnqueueUser {
    void add(QueueType type, OnlineUser onlineUser);
}
