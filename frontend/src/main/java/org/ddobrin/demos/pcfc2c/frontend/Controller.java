package org.ddobrin.demos.pcfc2c.frontend;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.ddobrin.demos.pcfc2c.frontend.config.BackendProperties;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import static java.net.InetAddress.getAllByName;

@org.springframework.stereotype.Controller
public class Controller {
    private final BackendClientService backendClientService;
    private final BackendProperties backendProps;
    private final InstanceInfo instanceInfo;

    public Controller(@NotNull BackendClientService backendClientService, @NotNull BackendProperties backendProps, @NotNull InstanceInfo instanceInfo) {
        super();
        Intrinsics.checkParameterIsNotNull(backendClientService, "backendClientService");
        Intrinsics.checkParameterIsNotNull(backendProps, "backendProps");
        Intrinsics.checkParameterIsNotNull(instanceInfo, "instanceInfo");

        this.backendClientService = backendClientService;
        this.backendProps = backendProps;
        this.instanceInfo = instanceInfo;
    }

    @GetMapping({"/"})
    public void index(@NotNull PrintWriter out) {
        Intrinsics.checkParameterIsNotNull(out, "out");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            this.doIndex(out);
        } finally {
            stopWatch.stop();
            out.println("Time spent: " + stopWatch.getTotalTimeMillis() + " ms");
        }
    }

    private void doIndex(PrintWriter out) {
        out.println("Welcome to the Cloud Foundry Container-to-container Java Demo");
        out.println("Frontend Instance: " + this.instanceInfo);
        out.println("Connecting to Backend Instance: " + this.backendProps.getHost() + ':' + this.backendProps.getPort());

        // invoke the backend service
        String response = this.backendClientService.ring(String.valueOf(this.instanceInfo));
        Intrinsics.checkExpressionValueIsNotNull(response, "backendClientService.ring(\"$instanceInfo\")");

        String formattedMessage = response.replace("\n", "\n  ");
        out.println("Received message from Backend Instance:\n  " + formattedMessage);
   }

    @GetMapping({"/backends"})
    @ResponseBody
    @NotNull
    public String backendAddresses() throws UnknownHostException {
        InetAddress[] hostIPs = getAllByName(this.backendProps.getHost());
        Intrinsics.checkExpressionValueIsNotNull(hostIPs, "InetAddress.getAllByName(backendProps.host)");

        return Arrays.stream(hostIPs).map(InetAddress::getHostAddress)
                .collect(Collectors.joining(" "));
    }

    @GetMapping({"/backend"})
    @ResponseBody
    @NotNull
    public String backendAddress() throws UnknownHostException {
        InetAddress hostIP = InetAddress.getByName(this.backendProps.getHost());
        Intrinsics.checkExpressionValueIsNotNull(hostIP, "InetAddress.getByName(backendProps.host)");

        String hostAddress = hostIP.getHostAddress();
        Intrinsics.checkExpressionValueIsNotNull(hostAddress, "InetAddress.getByName(ba…ndProps.host).hostAddress");

        return hostAddress;
    }
}
