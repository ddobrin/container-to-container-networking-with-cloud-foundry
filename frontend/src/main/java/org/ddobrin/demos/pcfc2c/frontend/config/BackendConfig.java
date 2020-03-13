package org.ddobrin.demos.pcfc2c.frontend.config;

import com.fasterxml.jackson.module.kotlin.ExtensionsKt;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retrofit.CircuitBreakerCallAdapter;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;

import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class BackendConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    @NotNull
    public BackendClientApi backendClientApi(@NotNull Retrofit retrofit) {
        Intrinsics.checkParameterIsNotNull(retrofit, "retrofit");
        Object backendClientApi = retrofit.create(BackendClientApi.class);
        Intrinsics.checkExpressionValueIsNotNull(backendClientApi, "retrofit.create(BackendClientApi::class)");

        return (BackendClientApi)backendClientApi;
    }

    @Bean
    @NotNull
    public Retrofit retrofit(@NotNull BackendProperties backendProps, @NotNull CircuitBreaker circuitBreaker, @NotNull OkHttpClient httpClient) {
        Intrinsics.checkParameterIsNotNull(backendProps, "backendProps");
        Intrinsics.checkParameterIsNotNull(circuitBreaker, "circuitBreaker");
        Intrinsics.checkParameterIsNotNull(httpClient, "httpClient");

        Retrofit instance = (new Builder()).client(httpClient).baseUrl("http://" + backendProps.getHost() + ':' + backendProps.getPort()).addConverterFactory((Factory)JacksonConverterFactory.create(ExtensionsKt.jacksonObjectMapper())).addCallAdapterFactory((retrofit2.CallAdapter.Factory)CircuitBreakerCallAdapter.of(circuitBreaker)).build();
        Intrinsics.checkExpressionValueIsNotNull(instance, "Retrofit.Builder()\n     …                 .build()");
        return instance;
    }

    @Bean
    @NotNull
    public OkHttpClient httpClient(@NotNull BackendProperties backendProps, @NotNull Interceptor clientSideLoadBalancer) {
        Intrinsics.checkParameterIsNotNull(backendProps, "backendProps");
        Intrinsics.checkParameterIsNotNull(clientSideLoadBalancer, "clientSideLoadBalancer");

        Boolean isLoadBalancingEnabled = backendProps.getLoadBalancing();
        Intrinsics.checkExpressionValueIsNotNull(isLoadBalancingEnabled, "backendProps.loadBalancing");
        OkHttpClient httpClient;
        if (isLoadBalancingEnabled) {
            this.logger.info("Client-side load-balancing is enabled");
            httpClient = (new okhttp3.OkHttpClient.Builder()).addInterceptor(clientSideLoadBalancer).build();
        } else {
            this.logger.info("Client-side load-balancing is disabled");
            httpClient = (new okhttp3.OkHttpClient.Builder()).build();
        }

        Intrinsics.checkExpressionValueIsNotNull(httpClient, "if (backendProps.loadBal…r().build()\n            }");
        return httpClient;
    }

    @Bean
    @NotNull
    public Interceptor clientSideLoadBalancer(@NotNull final BackendProperties backendProps) {
        Intrinsics.checkParameterIsNotNull(backendProps, "backendProps");

        return (Interceptor)(new Interceptor() {
            @NotNull
            public final Response intercept(@NotNull Chain chain) throws IOException {
                Intrinsics.checkParameterIsNotNull(chain, "chain");
                Request request = chain.request();
                Intrinsics.checkExpressionValueIsNotNull(request, "chain.request()");
                Response response;

                if (Intrinsics.areEqual(request.url().host(), backendProps.getHost())) {
                    // get the address by name
                    InetAddress address = InetAddress.getByName(backendProps.getHost());
                    Intrinsics.checkExpressionValueIsNotNull(address, "InetAddress.getByName(backendProps.host)");
                    String backendAddr = address.getHostAddress();
                    BackendConfig.this.logger.debug("Use load-balanced address for backend: {}", backendAddr);

                    // get the host
                    HttpUrl host = request.url().newBuilder().host(backendAddr).build();
                    Intrinsics.checkExpressionValueIsNotNull(host, "req.url().newBuilder().host(backendAddr).build()");
                    request = request.newBuilder().url(host).build();
                    Intrinsics.checkExpressionValueIsNotNull(request, "req.newBuilder().url(newUrl).build()");

                    // proceed with the invocation
                    Request newReq = request;
                    response = chain.proceed(newReq);
                } else {
                    response = chain.proceed(chain.request());
                }

                return response;
            }
        });
    }

    @Bean
    @NotNull
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.
                                        custom().
                                        ringBufferSizeInClosedState(2).
                                        waitDurationInOpenState(Duration.ofMillis(1000L)).
                                        recordExceptions(Throwable.class).
                                        build();
        CircuitBreaker var10000 = CircuitBreaker.of("backend", config);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "CircuitBreaker.of(\"backend\", config)");
        return var10000;
    }
}
