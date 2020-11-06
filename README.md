# distance-matrix-service

Microservice to calculate distance and time.

# Setup

* See in `docker-compose.yaml` the needed environment variables

## Running

* Gradle: use `gradle bootRun`
  * It starts the service on port `8080`
* Just use docker-compose up, with optional `--build` to start a container.
  * It starts the service on port `8082`

> **ATTENTION**: Make sure to adjust the port number that the host will listen to. Current is `8082` in docker-compose.yaml.

```console
$ docker-compose up --build
Building distance_matrix_service
Step 1/15 : ARG UNMAZEDBOOT_BUILDER_GIT_SHA=${UNMAZEDBOOT_BUILDER_GIT_SHA:-000000}
Step 2/15 : ARG UNMAZEDBOOT_BUILDER_GIT_BRANCH=${UNMAZEDBOOT_BUILDER_GIT_BRANCH:-master}
Step 3/15 : ARG UNMAZEDBOOT_BUILDER_GRADLE_BUILD_CMD="gradle build -x test"
Step 4/15 : ARG UNMAZEDBOOT_BUILDER_DIR="build/libs"
Step 5/15 : ARG UNMAZEDBOOT_BUILDER_PACKAGE_EXTENSION="jar"
Step 6/15 : ARG UNMAZEDBOOT_BUILDER_GRADLE_VERSION=${UNMAZEDBOOT_BUILDER_GRADLE_VERSION:-latest}
Step 7/15 : ARG UNMAZEDBOOT_LINKER_VERSION=${UNMAZEDBOOT_LINKER_VERSION:-latest}
Step 8/15 : ARG UNMAZEDBOOT_RUNNER_PORT="8080"
Step 9/15 : ARG UNMAZEDBOOT_RUNNER_VERSION=${UNMAZEDBOOT_RUNNER_VERSION:-latest}
Step 10/15 : FROM intuit/unmazedboot-builder-gradle:${UNMAZEDBOOT_BUILDER_GRADLE_VERSION} as unmazedboot-builder-artifacts
# Executing 18 build triggers
 ---> Using cache
 ---> ffcf918083f6

Step 11/15 : FROM intuit/unmazedboot-linker:${UNMAZEDBOOT_LINKER_VERSION} as unmazedboot-jdk-linker
# Executing 7 build triggers
 ---> Using cache
 ---> efb7dbdd3065

Step 12/15 : FROM intuit/unmazedboot-runner:${UNMAZEDBOOT_RUNNER_VERSION}
# Executing 39 build triggers
 ---> Using cache
 ---> 01e85f8f9bce
Step 13/15 : COPY --from=unmazedboot-builder-artifacts /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts /etc/ssl/certs/java/cacerts
 ---> Using cache
 ---> 6700acbb133d
Step 14/15 : RUN rm -f /opt/jdk-custom/jre/lib/security/cacerts
 ---> Using cache
 ---> 9048affad1cc
Step 15/15 : RUN ln -s /etc/ssl/certs/java/cacerts /opt/jdk-custom/jre/lib/security/cacerts
 ---> Using cache
 ---> cd2f06fdc366

Successfully built cd2f06fdc366
Successfully tagged registry.gitlab.com/supercash/services/distance-matrix-service:develop
Recreating distance-matrix-service_distance_matrix_service_1 ... done
Attaching to distance-matrix-service_distance_matrix_service_1
distance_matrix_service_1  |
distance_matrix_service_1  | => Initializing SpringBoot Runner 'start.sh'
distance_matrix_service_1  |
distance_matrix_service_1  | ########## Running init scripts at '/runtime/init'
distance_matrix_service_1  |
distance_matrix_service_1  |
distance_matrix_service_1  | ########## Processing source hooks at '/runtime/sources'
distance_matrix_service_1  |
distance_matrix_service_1  | [1] source /runtime/sources/springboot.sh
distance_matrix_service_1  |
distance_matrix_service_1  | => Processing JAVA_OPTS hooks at /runtime/java-opts
distance_matrix_service_1  |
distance_matrix_service_1  | [1] JAVA_OPTS << ./java-opts/vminfo.opt
distance_matrix_service_1  | [2] JAVA_OPTS << ./java-opts/docker.opt
distance_matrix_service_1  | [3] JAVA_OPTS << ./java-opts/jdk-custom-debug.opt
distance_matrix_service_1  | [4] JAVA_OPTS << ./java-opts/springboot.opt
distance_matrix_service_1  |
distance_matrix_service_1  | Exporting found opts JAVA_OPTS= -showversion -XshowSettings:vm -XX:+UnlockExperimentalVMOptions --show-module-resolution -Djava.security.egd=file:/dev/./urandom
distance_matrix_service_1  |
distance_matrix_service_1  | ####### Starting the app #######
distance_matrix_service_1  |
distance_matrix_service_1  | java $JAVA_OPTS -jar /runtime/server.jar $JAR_OPTS
distance_matrix_service_1  |
distance_matrix_service_1  | root java.sql jrt:/java.sql
distance_matrix_service_1  | root java.transaction.xa jrt:/java.transaction.xa
distance_matrix_service_1  | root java.logging jrt:/java.logging
distance_matrix_service_1  | root java.xml jrt:/java.xml
distance_matrix_service_1  | root java.management jrt:/java.management
distance_matrix_service_1  | root jdk.unsupported jrt:/jdk.unsupported
distance_matrix_service_1  | root java.datatransfer jrt:/java.datatransfer
distance_matrix_service_1  | root java.instrument jrt:/java.instrument
distance_matrix_service_1  | root java.security.jgss jrt:/java.security.jgss
distance_matrix_service_1  | root java.desktop jrt:/java.desktop
distance_matrix_service_1  | root java.naming jrt:/java.naming
distance_matrix_service_1  | root java.prefs jrt:/java.prefs
distance_matrix_service_1  | root java.security.sasl jrt:/java.security.sasl
distance_matrix_service_1  | root java.base jrt:/java.base
distance_matrix_service_1  | java.security.sasl requires java.logging jrt:/java.logging
distance_matrix_service_1  | java.prefs requires java.xml jrt:/java.xml
distance_matrix_service_1  | java.naming requires java.security.sasl jrt:/java.security.sasl
distance_matrix_service_1  | java.desktop requires java.datatransfer jrt:/java.datatransfer
distance_matrix_service_1  | java.desktop requires java.xml jrt:/java.xml
distance_matrix_service_1  | java.desktop requires java.prefs jrt:/java.prefs
distance_matrix_service_1  | java.security.jgss requires java.naming jrt:/java.naming
distance_matrix_service_1  | java.sql requires java.logging jrt:/java.logging
distance_matrix_service_1  | java.sql requires java.xml jrt:/java.xml
distance_matrix_service_1  | java.sql requires java.transaction.xa jrt:/java.transaction.xa
distance_matrix_service_1  | java.datatransfer binds java.desktop jrt:/java.desktop
distance_matrix_service_1  | java.base binds java.naming jrt:/java.naming
distance_matrix_service_1  | java.base binds java.security.sasl jrt:/java.security.sasl
distance_matrix_service_1  | java.base binds java.security.jgss jrt:/java.security.jgss
distance_matrix_service_1  | java.base binds java.desktop jrt:/java.desktop
distance_matrix_service_1  | java.base binds java.management jrt:/java.management
distance_matrix_service_1  | java.base binds java.logging jrt:/java.logging
distance_matrix_service_1  | VM settings:
distance_matrix_service_1  |     Max. Heap Size (Estimated): 2.93G
distance_matrix_service_1  |     Using VM: OpenJDK 64-Bit Server VM
distance_matrix_service_1  |
distance_matrix_service_1  | openjdk version "11-ea" 2018-09-25
distance_matrix_service_1  | OpenJDK Runtime Environment 18.9 (build 11-ea+25)
distance_matrix_service_1  | OpenJDK 64-Bit Server VM 18.9 (build 11-ea+25, mixed mode)
distance_matrix_service_1  |
distance_matrix_service_1  |   .   ____          _            __ _ _
distance_matrix_service_1  |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
distance_matrix_service_1  | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
distance_matrix_service_1  |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
distance_matrix_service_1  |   '  |____| .__|_| |_|_| |_\__, | / / / /
distance_matrix_service_1  |  =========|_|==============|___/=/_/_/_/
distance_matrix_service_1  |  :: Spring Boot ::        (v2.1.4.RELEASE)
distance_matrix_service_1  |
distance_matrix_service_1  | 2020-11-01 14:33:27.165  INFO 16 --- [           main] s.d.DistanceMatrixApplication            : Starting DistanceMatrixApplication on 10bcaa8ae375 with PID 16 (/runtime/server.jar started by root in /runtime)
distance_matrix_service_1  | 2020-11-01 14:33:27.170  INFO 16 --- [           main] s.d.DistanceMatrixApplication            : No active profile set, falling back to default profiles: default
distance_matrix_service_1  | 2020-11-01 14:33:28.564  INFO 16 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
distance_matrix_service_1  | 2020-11-01 14:33:28.620  INFO 16 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
distance_matrix_service_1  | 2020-11-01 14:33:28.620  INFO 16 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.17]
distance_matrix_service_1  | 2020-11-01 14:33:28.726  INFO 16 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
distance_matrix_service_1  | 2020-11-01 14:33:28.726  INFO 16 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1472 ms
distance_matrix_service_1  | 2020-11-01 14:33:28.990  INFO 16 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
distance_matrix_service_1  | 2020-11-01 14:33:29.293  INFO 16 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
distance_matrix_service_1  | 2020-11-01 14:33:29.301  INFO 16 --- [           main] s.d.DistanceMatrixApplication            : Started DistanceMatrixApplication in 2.762 seconds (JVM running for 3.447)
distance_matrix_service_1  | 2020-11-01 14:33:30.655  INFO 16 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
distance_matrix_service_1  | 2020-11-01 14:33:30.656  INFO 16 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
distance_matrix_service_1  | 2020-11-01 14:33:30.663  INFO 16 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 7 ms
distance_matrix_service_1  | 2020-11-01 14:33:32.141  INFO 16 --- [nio-8080-exec-1] s.d.DistanceMatrixApplication            : Distance: 257055 meters
distance_matrix_service_1  | 2020-11-01 14:33:32.141  INFO 16 --- [nio-8080-exec-1] s.d.DistanceMatrixApplication            : Time: 13516 seconds
^CGracefully stopping... (press Ctrl+C again to force)
Stopping distance-matrix-service_distance_matrix_service_1   ...
Killing distance-matrix-service_distance_matrix_service_1    ... done
```

# API

* Go to the Swagger URL to discover the APIs available
  * http://localhost:8082/swagger-ui/
  * Keep the end `/` or else it fails to open
* The OpenAPI endpoint for discovery is `/swagger/docs/v2`
  * This is to support Gloo API Gateway (https://docs.solo.io/gloo/latest/installation/advanced_configuration/fds_mode/)
* Stubs: located at https://gitlab.com/supercash/clients/distance-matrix-client-resttemplate
  * Generated by https://github.com/marcellodesales/swagger-client-package-repo-gen

## API Examples

| Method | Endpoint | Headers | Payload |
| ----- | ----- | ---- | ----- |
| POST  | /v1/distancematrix | `content-type: application/json` | { "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" } |

* Response

| Headers | Payload |
| -- | -- |
| `Content-Type: application/json` | {"distance":257055,"time":13516} |

## Example

```console
$ curl -i localhost:8080/v1/distancematrix \
    -H 'supercash_cid: marcello' \
    -H 'supercash_tid: 123' \
    -H 'content-type: application/json' \
    -d '{ "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" }'
HTTP/1.1 200
X-Trace-ID: 3e635cc4b8771243
api-version: v1
supercash_tid: 123
supercash_cid: marcello
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 06 Nov 2020 14:19:18 GMT

{"distance":257055,"time":13519}%
```

# Automated Tests

* Gradle tests using:
  * Junit 5 for unit tests
  * Jacoco for coverage: shows percentage of coverage and generates reports

> Note: make sure to use the same version as in `tests-docker-compose.yaml`.

```console
$ gradle tests check
```
* Using Docker to test
  * This is the same command used in CI/CD
  * Reports will be stored under in the mapped volume in `tests-docker-compose.yaml`

```
$ docker-compose -f tests.docker-compose.yaml up
```

* In Gitlab for code coverage: https://docs.gitlab.com/ee/ci/pipelines/settings.html#test-coverage-parsing
  * https://gitlab.com/supercash/services/distance-matrix-service/-/settings/ci_cd#js-general-pipeline-settings

```
$ gradle check

Coverage summary:
distance-matrix-service:   87.4%

BUILD SUCCESSFUL in 1s
10 actionable tasks: 1 executed, 9 up-to-date
```

# Observability

* How to relate Logging, Tracing and Metrics from the service.

## Tracing

* Start the `tools/zipkin` docker-compose stack, open the URL http://localhost:9411/zipkin
* Start the this service and make the requests to start using tracing

After making requests, go to `Zipkin screen -> Find a Trace -> [+] -> serviceName=distance-matrix-service -> [Run Query]`

* It will show the traces for the requests
* Expand the traces, copy the value of `Trace ID`
  * Paste at the field `Search by trace ID` and press ENTER.

The traces will show with the spans.

* In the client-side, you can see the `Trace ID` through the HTTP Response Header `X-Trace-ID`. The example as shown above.

```console
$ curl -i localhost:8080/v1/distancematrix \
    -H 'supercash_cid: marcello' \
    -H 'supercash_tid: 123' \
    -H 'content-type: application/json' \
    -d '{ "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" }'
HTTP/1.1 200
X-Trace-ID: 3e635cc4b8771243
api-version: v1
supercash_tid: 123
supercash_cid: marcello
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 06 Nov 2020 14:19:18 GMT

{"distance":257055,"time":13519}%
```

# Troubleshooting

* Rotate Google Maps token when getting errors about the token
* https://developers.google.com/maps/gmp-get-started

```console
$ curl -i localhost:8082/v1/distancematrix -H 'content-type: application/json' -d '{ "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" }'
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sun, 01 Nov 2020 14:23:56 GMT

{"description":"You must enable Billing on the Google Cloud Project at https://console.cloud.google.com/project/_/billing/enable Learn more at https://developers.google.com/maps/gmp-get-started","error":500}%
```
