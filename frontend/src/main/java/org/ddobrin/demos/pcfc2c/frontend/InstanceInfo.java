package org.ddobrin.demos.pcfc2c.frontend;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class InstanceInfo {
    @NotNull
    private final String applicationName;
    private final int instanceIndex;

    public InstanceInfo(@NotNull String applicationName, int instanceIndex, @NotNull String ip) {
        super();

        Intrinsics.checkParameterIsNotNull(applicationName, "applicationName");
        Intrinsics.checkParameterIsNotNull(ip, "ip");

        this.applicationName = applicationName;
        this.instanceIndex = instanceIndex;
        this.ip = ip;
    }
    @NotNull
    private final String ip;

    @NotNull
    public String toString() {
        return '[' + this.applicationName + '/' + this.instanceIndex + ' ' + this.ip + ']';
    }

    @NotNull
    public final String getApplicationName() {
        return this.applicationName;
    }

    public final int getInstanceIndex() {
        return this.instanceIndex;
    }

    @NotNull
    public final String getIp() {
        return this.ip;
    }
}
