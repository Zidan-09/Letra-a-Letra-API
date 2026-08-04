package com.letraaletra.api.features.admin.application.port;

import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;

public interface SystemStatusService {
    GetSystemStatusOutput handle();
}
