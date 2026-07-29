package com.letraaletra.api.features.admin.application.output;

import com.letraaletra.api.features.admin.domain.Admin;
import org.springframework.data.domain.Page;

public record GetAdminsOutput(
        Page<Admin> admins
) {
}
