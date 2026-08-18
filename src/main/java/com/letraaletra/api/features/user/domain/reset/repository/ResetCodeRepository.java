package com.letraaletra.api.features.user.domain.reset.repository;

import com.letraaletra.api.features.user.application.port.InvalidateCode;

public interface ResetCodeRepository extends
        SaveResetCode,
        FindResetCode,
        InvalidateCode
{}
