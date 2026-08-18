package com.letraaletra.api.features.admin.application.port;

import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;

public interface ApplicationStatusService {
    GetApplicationStatusOutput handle();
}
