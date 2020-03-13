package org.ddobrin.demos.pcfc2c.frontend.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.core.EventConsumer;
import javax.annotation.PostConstruct;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerInit {
    private final Logger logger;
    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerInit(@NotNull CircuitBreaker circuitBreaker) {
        super();
        Intrinsics.checkParameterIsNotNull(circuitBreaker, "circuitBreaker");
        this.circuitBreaker = circuitBreaker;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @PostConstruct
    public void init() {
        this.circuitBreaker.getEventPublisher().onStateTransition((EventConsumer)(new EventConsumer() {
            public void consumeEvent(Object ev) {
                this.consumeEvent((CircuitBreakerOnStateTransitionEvent)ev);
            }

            public final void consumeEvent(CircuitBreakerOnStateTransitionEvent e) {
                CircuitBreakerInit.this.logger.debug("Circuit breaker state updated: {}", e);
            }
        })).onError((EventConsumer)(new EventConsumer() {
            public void consumeEvent(Object ev) {
                this.consumeEvent((CircuitBreakerOnErrorEvent)ev);
            }

            public final void consumeEvent(CircuitBreakerOnErrorEvent e) {
                CircuitBreakerInit.this.logger.warn("Circuit breaker error: {}", e);
            }
        }));
    }
}
