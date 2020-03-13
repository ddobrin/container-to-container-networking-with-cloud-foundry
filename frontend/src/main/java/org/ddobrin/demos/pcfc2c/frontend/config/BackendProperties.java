package org.ddobrin.demos.pcfc2c.frontend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Backend configuration properties.
 */
@Configuration
@ConfigurationProperties("backend")
public class BackendProperties {
    String host = "pcf-c2c-java-backend";
    Integer port = 8080;
    Boolean loadBalancing = false;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getLoadBalancing() {
        return loadBalancing;
    }

    public void setLoadBalancing(Boolean loadBalancing) {
        this.loadBalancing = loadBalancing;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}