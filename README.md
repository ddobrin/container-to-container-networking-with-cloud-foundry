# Java Demo for Pivotal Cloud Foundry Container-to-Container Communication

This project illustrates the use of [BOSH-DNS](https://bosh.io/docs/dns) for Java-based microservices
deployed on [Pivotal Cloud Foundry](https://pivotal.io/platform).

## Why BOSH-DNS for container-to-container communication
Thanks to BOSH-DNS, microservices running on PCF do not require a service registry for discovery and service invocation (such as
[Netflix Eureka](https://github.com/Netflix/eureka) or [Hashicorp Consul](https://www.consul.io)),
since all apps owning a route on the domain `apps.internal` can be resolved by all apps running
on the platform. 

For example, if an app has a route `mynewapp.apps.internal`, any app can access it
from within the platform. 

Moreover, these apps do not require a public route in order to be
accessible from apps, and do not use the `gorouter`, thus offering also better performance.

## Enable container-to-container access within PCF-based apps
An app making a direct connection to another app requires a network policy.
This [network policy](https://docs.cloudfoundry.org/concepts/understand-cf-networking.html)
allows a container app to open a connection to another container:
```shell
# app-source - the consumer application
# app-target - the provide application, aka the target app
# port - the port where the target appp is being accessed

> cf add-network-policy <app-source> --destination-app <app-target> --protocol tcp --port <port>
```

Using BOSH-DNS and container-to-container networking, your microservices do not require any external libraries
to discover service endpoints.

If you are using these features, __**you do not need to install**__ the
[Spring Cloud Service tile](https://docs.pivotal.io/spring-cloud-services/2-1/common/index.html)
on PCF, since BOSH-DNS is a core platform feature.

**This Java demo project does not use Spring Cloud Netflix** and does not leverage Hystrix, no Eureka, no Ribbon.

## REST-based service invocation
REST calls are made using
[Retrofit2](https://square.github.io/retrofit), and network errors are managed using
a circuit breaker pattern implemented by [Resilience4j](https://github.com/resilience4j/resilience4j).
This app is fault tolerant, and can be scaled-out (more instances) and scaled-up (more CPU/memory)
with zero downtime.

All these features are available for all apps with any language (not only Spring Boot apps
written in Java).

Development teams can use other REST clients or circuit breakers of their choosing.

# How to run the demo apps

This demo project consists of two components:
 - `pcf-c2c-java-backend`: a microservice exposing a REST API
 - `pcf-c2c-java-frontend`: a microservice connecting to backend instances
 
You can use this project on any PCF 2.2+ instances, such as [Pivotal Web Service](https://run.pivotal.io). It has been tested on version 2.8, the latest release at the time of this writing.

Compile this project with Maven and a JDK 8, and deploy these apps to Pivotal Cloud Foundry:
```shell
> ./mvnw clean package && cf push
```

The Frontend Java app is the only one exposing a public endpoint: 
```shell
# cUrl command
> curl -s http://pcf-c2c-java-frontend-<RANDOM>.domain.com

# for example, deploying with a random route, the frontend app call might receive this sample response:
# request

> curl https://pcf-c2c-java-frontend-chipper-parrot-vn.cfapps.io/ 

# sample response
  Welcome to the Cloud Foundry Container-to-container Java Demo
  Frontend Instance: [pcf-c2c-java-frontend/0 10.251.70.14]
  Connecting to Backend Instance: pcf-c2c-java-backend.apps.internal:8080
  Received message from Backend Instance:
    No backend service available
  Time spent: 10 ms
```

As you can see, Backend app instances are not seen by Frontend app instances.
You need to "allow" connections between frontend app instances and backend app instances:
```shell
> cf add-network-policy pcf-c2c-java-frontend --destination-app pcf-c2c-java-backend --protocol tcp --port 8080
```

This command enables container-to-container networking between app instances, from the Frontend Java app
instances to the Backend Java app instances.

As soon as this network policy is applied (it can take up to ten seconds), Backend app instances
are now accessible by frontend app instances:
```shell
# cUrl command
> curl -s http://pcf-c2c-java-frontend-<RANDOM>.domain.com

# for example, deploying with a random route, the frontend app call might receive this sample response:
# request
> curl https://pcf-c2c-java-frontend-chipper-parrot-vn.cfapps.io/ 

# sample response
  Welcome to the Cloud Foundry Container-to-container Java Demo
  Frontend Instance: [pcf-c2c-java-frontend/0 10.250.186.172]
  Connecting to Backend Instance: pcf-c2c-java-backend.apps.internal:8080
  Received message from Backend Instance:
    [pcf-c2c-java-backend/1 10.240.219.2] says:
    Thank you for coming, [pcf-c2c-java-frontend/0 10.250.186.172]!
    Visitor count: 1
  Time spent: 244 ms
```

## Load balancing requests

The demo startes the Backend Java app with 2 instances and provisions 2 endpoints to allow you to randomly pick the host address of a backend instance or to list the host address for all backend instances

For exmple:
```
# list host address for ALL backend instances 
> curl https://pcf-c2c-java-frontend-chipper-parrot-vn.cfapps.io/backends

# sample response
List of ALL Backend instance host addresses:
 10.243.232.119,  10.240.219.2

# list host address for one of them picked up irandomly
> curl https://pcf-c2c-java-frontend-chipper-parrot-vn.cfapps.io/backend

# sample response
Backend instance host address: 10.240.219.2

or 

Backend instance host address: 10.243.232.119
```

If you kill one of the Backend app instances used by a Frontend app instance, another Backend app instance
will automatically be resolved by BOSH-DNS the next time a Frontend app instance is making a
REST call. 

Please note that there is no app downtime while a new backend app is being used.

Client-side load-balancing is done without using an external
library (such as Netflix Ribbon). A custom
[OkHttp3 Interceptor](https://square.github.io/okhttp/3.x/okhttp/okhttp3/Interceptor.html)
implementation (used by Retrofit2) is included to load balance 
network requests. This implementation simply uses backend
IP addresses given by BOSH-DNS, using a call to
[InetAddress.getByName()](https://docs.oracle.com/javase/8/docs/api/java/net/InetAddress.html#getByName-java.lang.String-).

You can disable client-side load-balancing by overriding the
property `backend.loadBalancing`, which is set to
`true` by default. You may also set this property using an
environment variable:
```shell
> cf set-env pcf-c2c-java-frontend BACKEND_LOADBALANCING false

# use 'cf restage pcf-c2c-java-frontend' to ensure your env variable changes take effect
> cf restage pcf-c2c-java-frontend
```

