package com.letraaletra.api.features.admin.domain.repository;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminsPage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GetAdmins {
    Page<Admin> getAdmins(AdminsPage page);
}
