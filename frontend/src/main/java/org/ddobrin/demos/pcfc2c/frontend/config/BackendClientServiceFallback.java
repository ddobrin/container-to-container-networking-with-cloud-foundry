package org.ddobrin.demos.pcfc2c.frontend.config;

import org.ddobrin.demos.pcfc2c.frontend.BackendClientService;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("fallback")
public class BackendClientServiceFallback implements BackendClientService {
    @NotNull
    public String ring(@NotNull String visitor) {
        Intrinsics.checkParameterIsNotNull(visitor, "visitor");
        return "No backend service available";
    }
}