package org.ddobrin.demos.pcfc2c.frontend.config;

import org.ddobrin.demos.pcfc2c.frontend.BackendClientService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;

import java.io.IOException;
import java.util.function.Supplier;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import retrofit2.Response;

@Component
@Primary
public class BackendClientServiceImpl implements BackendClientService {
    private final BackendClientApi api;
    private final BackendClientService fallback;
    private final CircuitBreaker circuitBreaker;

    public BackendClientServiceImpl(@NotNull BackendClientApi api, @Qualifier("fallback") @NotNull BackendClientService fallback, @NotNull CircuitBreaker circuitBreaker) {
        super();
        Intrinsics.checkParameterIsNotNull(api, "api");
        Intrinsics.checkParameterIsNotNull(fallback, "fallback");
        Intrinsics.checkParameterIsNotNull(circuitBreaker, "circuitBreaker");
        this.api = api;
        this.fallback = fallback;
        this.circuitBreaker = circuitBreaker;
    }

    @NotNull
    @Override
    public String ring(@NotNull final String visitor) {
        Intrinsics.checkParameterIsNotNull(visitor, "visitor");

        Supplier<String> supplier = CircuitBreaker.decorateSupplier(this.circuitBreaker,
                (Supplier<String>)(new Supplier() {
                    @NotNull
                    public String get() {
                        try {
                            return BackendClientServiceImpl.this.api.ring(visitor).execute().body().getMessage();
                        } catch (IOException e) {
                            return BackendClientServiceImpl.this.fallback.ring(visitor);
                        }
                    }
                }));

        return Try.ofSupplier(supplier).get();
    }

    private static final Object bodyNotNull(@NotNull Response response) throws Throwable {
        Object var10000 = response.body();
        if (var10000 != null) {
            return var10000;
        } else {
            throw new IllegalStateException("No body set in response");
        }
    }
}