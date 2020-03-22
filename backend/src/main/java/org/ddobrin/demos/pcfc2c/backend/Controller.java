package org.ddobrin.demos.pcfc2c.backend;

import java.util.concurrent.atomic.AtomicLong;
import javax.validation.constraints.NotEmpty;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private final AtomicLong counter;
    private final Logger logger;
    private final InstanceInfo instanceInfo;

    @GetMapping({"/ring"})
    @NotNull
    public Controller.DoorbellResponse ringDoorbell(@NotEmpty @RequestParam("visitor") @NotNull String visitor) {
        Intrinsics.checkParameterIsNotNull(visitor, "visitor");
        this.logger.info("Someone is at the door: " + visitor);
        long c = this.counter.incrementAndGet();
        return new Controller.DoorbellResponse(this.instanceInfo + " says:\nThank you for coming, " + visitor + "!\nVisitor count: " + c);
    }

    public Controller(@NotNull InstanceInfo instanceInfo) {
        super();
        Intrinsics.checkParameterIsNotNull(instanceInfo, "instanceInfo");
        this.instanceInfo = instanceInfo;
        this.counter = new AtomicLong();
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public static final class DoorbellResponse {
        @NotNull
        private final String message;

        @NotNull
        public final String getMessage() {
            return this.message;
        }

        public DoorbellResponse(@NotNull String message) {
            super();
            Intrinsics.checkParameterIsNotNull(message, "message");

            this.message = message;
        }

        @NotNull
        public final Controller.DoorbellResponse copy(@NotNull String message) {
            Intrinsics.checkParameterIsNotNull(message, "message");
            return new Controller.DoorbellResponse(message);
        }

        @NotNull
        public String toString() {
            return "DoorbellResponse(message=" + this.message + ")";
        }

        public int hashCode() {
            return this.message != null ? this.message.hashCode() : 0;
        }

        public boolean equals(@Nullable Object var1) {
            if (this != var1) {
                if (var1 instanceof Controller.DoorbellResponse) {
                    Controller.DoorbellResponse var2 = (Controller.DoorbellResponse)var1;
                    if (Intrinsics.areEqual(this.message, var2.message)) {
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
