package com.letraaletra.api.features.admin.domain.repository;

import org.springframework.data.domain.Page;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminsPage;

public interface GetAdmins {
    Page<Admin> getAdmins(AdminsPage page);
}
