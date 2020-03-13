package org.ddobrin.demos.pcfc2c.frontend.config;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BackendClientApi {
    @GET("ring?")
    @NotNull
    Call<RingResponse> ring(@Query("visitor") @NotNull String var1);

    public static final class RingResponse {
        public RingResponse() {
            this.message = "default constructor::message";
        }

        public RingResponse(@NotNull String message) {
            super();
            Intrinsics.checkParameterIsNotNull(message, "message");
            this.message = message;
        }

        @NotNull
        private String message;

        @NotNull
        public final void setMessage(String message){
            this.message = message;
        }

        @NotNull
        public final String getMessage() {
            return this.message;
        }

        @NotNull
        public final BackendClientApi.RingResponse copy(@NotNull String message) {
            Intrinsics.checkParameterIsNotNull(message, "message");
            return new BackendClientApi.RingResponse(message);
        }

        @NotNull
        public String toString() {
            return "RingResponse(message=" + this.message + ")";
        }

        public int hashCode() {
            return this.message != null ? this.message.hashCode() : 0;
        }

        public boolean equals(@Nullable Object var1) {
            if (this != var1) {
                if (var1 instanceof BackendClientApi.RingResponse) {
                    BackendClientApi.RingResponse var2 = (BackendClientApi.RingResponse)var1;
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
