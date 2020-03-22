// InstanceInfoConfig.java
package org.ddobrin.demos.pcfc2c.backend.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ddobrin.demos.pcfc2c.backend.InstanceInfo;

import java.io.IOException;
import java.net.InetAddress;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InstanceInfoConfig {
    @Value("${spring.application.name}")
    private String springApplicationName;

    @Bean
    @NotNull
    public InstanceInfo instanceInfo(@NotNull ObjectMapper objectMapper) throws IOException {
        Intrinsics.checkParameterIsNotNull(objectMapper, "objectMapper");
        String envVar = System.getenv("CF_INSTANCE_INDEX");
        if (envVar == null) {
            envVar = "-1";
        }

        int instanceIndex = Integer.parseInt(envVar);
        String applicationName = null;
        String vcapApplicationJson = System.getenv("VCAP_APPLICATION");
        if (vcapApplicationJson == null) {
            envVar = this.springApplicationName;
            if (envVar == null) {
                Intrinsics.throwUninitializedPropertyAccessException("springApplicationName");
            }

            applicationName = envVar;
        } else {
            JsonNode node = objectMapper.readTree(System.getenv("VCAP_APPLICATION"));
            envVar = node.get("application_name").asText();
            if (envVar == null) {
                envVar = this.springApplicationName;
                if (envVar == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("springApplicationName");
                }
            }

            applicationName = envVar;
        }

        InetAddress localHost = InetAddress.getLocalHost();
        Intrinsics.checkExpressionValueIsNotNull(localHost, "InetAddress.getLocalHost()");
        String ip = localHost.getHostAddress();
        return new InstanceInfo(applicationName, instanceIndex, ip);
    }

    @JsonIgnoreProperties(
            ignoreUnknown = true
    )
    private static final class VcapApplication {
        @Nullable
        private final String applicationName;

        @Nullable
        public final String getApplicationName() {
            return this.applicationName;
        }

        public VcapApplication(@JsonProperty("application_name") @Nullable String applicationName) {
            this.applicationName = applicationName;
        }

        @Nullable
        public final String component1() {
            return this.applicationName;
        }

        @NotNull
        public final InstanceInfoConfig.VcapApplication copy(@JsonProperty("application_name") @Nullable String applicationName) {
            return new InstanceInfoConfig.VcapApplication(applicationName);
        }

        @NotNull
        public String toString() {
            return "VcapApplication(applicationName=" + this.applicationName + ")";
        }

        public int hashCode() {
            return this.applicationName != null ? this.applicationName.hashCode() : 0;
        }

        public boolean equals(@Nullable Object var1) {
            if (this != var1) {
                if (var1 instanceof InstanceInfoConfig.VcapApplication) {
                    InstanceInfoConfig.VcapApplication var2 = (InstanceInfoConfig.VcapApplication)var1;
                    if (Intrinsics.areEqual(this.applicationName, var2.applicationName)) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
        }
    }
}
