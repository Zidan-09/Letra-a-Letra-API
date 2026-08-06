package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.GetUsersInput;
import com.letraaletra.api.features.user.application.output.GetUsersOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetUsersUseCase implements UseCase<GetUsersInput, GetUsersOutput> {
    private final UserRepository userRepository;
    private final AdminChecker adminChecker;

    public GetUsersUseCase(
            UserRepository userRepository,
            AdminChecker adminChecker
    ) {
        this.userRepository = userRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetUsersOutput execute(GetUsersInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.VIEW);

        Page<User> users = userRepository.get(
                new UsersPage(input.page(), input.size(), input.sort())
        );

        return new GetUsersOutput(users);
    }
}
