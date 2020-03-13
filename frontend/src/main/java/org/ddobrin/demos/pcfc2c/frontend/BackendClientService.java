package org.ddobrin.demos.pcfc2c.frontend;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for the backend service
 */
public interface BackendClientService {
    @NotNull
    String ring(@NotNull String var1);
}