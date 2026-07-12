package com.letraaletra.api.features.admin.application.output;

public record GetApplicationStatusOutput(
        long users,
        long usersOnline,
        long games
) {
}
