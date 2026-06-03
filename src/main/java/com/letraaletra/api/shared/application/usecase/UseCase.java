package com.letraaletra.api.shared.application.usecase;

public interface UseCase<Input, Output> {
    Output execute(Input command);
}
