package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
import com.letraaletra.api.features.user.application.port.UserEquippedItemsProvider;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class FindUserByUsernameUseCase implements UseCase<FindUserByUsernameInput, FindUserByUsernameOutput> {
    private final UserRepository userRepository;
    private final UserEquippedItemsProvider equippedItemsProvider;

    public FindUserByUsernameUseCase(
            UserRepository userRepository,
            UserEquippedItemsProvider equippedItemsProvider
    ) {
        this.userRepository = userRepository;
        this.equippedItemsProvider = equippedItemsProvider;
    }

    @Override
    public FindUserByUsernameOutput execute(FindUserByUsernameInput input) {
        Page<User> users = userRepository.search(
                input.username(),
                new UsersPage(input.page(), input.size(), input.sort())
        );

        return new FindUserByUsernameOutput(users, equippedItemsProvider.equippedFor(users.getContent().stream().map(User::getUserId).toList()));
    }
}
