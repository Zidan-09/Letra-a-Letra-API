package com.letraaletra.api.shared.application.usecase;

public interface UseCaseWithoutOutput<Input> {
    void execute(Input command);
}
