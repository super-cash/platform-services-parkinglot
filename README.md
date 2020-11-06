# distance-matrix-service

Microservice to calculate distance and time.

# Setup

* See in `docker-compose.yaml` the needed environment variables
* The CI/CD infrastructure will create and push the docker images to the Container Registry.
  * How to load the image locally or in Kubernetes: https://gitlab.com/supercash/infra/k8s-cluster/-/wikis/Docker-Images

## Building and Running

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
Successfully tagged supercash/distance-matrix-service:latest
Creating distance-matrix-service_distance-matrix-service_1 ... done
Attaching to distance-matrix-service_distance-matrix-service_1
distance-matrix-service_1  |
distance-matrix-service_1  | => Initializing SpringBoot Runner 'start.sh'
distance-matrix-service_1  |
distance-matrix-service_1  | ########## Running init scripts at '/runtime/init'
distance-matrix-service_1  |
distance-matrix-service_1  |
distance-matrix-service_1  | ########## Processing source hooks at '/runtime/sources'
distance-matrix-service_1  |
distance-matrix-service_1  | [1] source /runtime/sources/springboot.sh
distance-matrix-service_1  |
distance-matrix-service_1  | => Processing JAVA_OPTS hooks at /runtime/java-opts
distance-matrix-service_1  |
distance-matrix-service_1  | [1] JAVA_OPTS << ./java-opts/vminfo.opt
distance-matrix-service_1  | [2] JAVA_OPTS << ./java-opts/docker.opt
distance-matrix-service_1  | [3] JAVA_OPTS << ./java-opts/jdk-custom-debug.opt
distance-matrix-service_1  | [4] JAVA_OPTS << ./java-opts/springboot.opt
distance-matrix-service_1  |
distance-matrix-service_1  | Exporting found opts JAVA_OPTS= -showversion -XshowSettings:vm -XX:+UnlockExperimentalVMOptions --show-module-resolution -Djava.security.egd=file:/dev/./urandom
distance-matrix-service_1  |
distance-matrix-service_1  | ####### Starting the app #######
distance-matrix-service_1  |
distance-matrix-service_1  | java $JAVA_OPTS -jar /runtime/server.jar $JAR_OPTS
distance-matrix-service_1  |
distance-matrix-service_1  | root java.sql jrt:/java.sql
distance-matrix-service_1  | root java.logging jrt:/java.logging
distance-matrix-service_1  | root java.transaction.xa jrt:/java.transaction.xa
distance-matrix-service_1  | root java.management jrt:/java.management
distance-matrix-service_1  | root java.xml jrt:/java.xml
distance-matrix-service_1  | root jdk.unsupported jrt:/jdk.unsupported
distance-matrix-service_1  | root java.datatransfer jrt:/java.datatransfer
distance-matrix-service_1  | root java.instrument jrt:/java.instrument
distance-matrix-service_1  | root java.security.jgss jrt:/java.security.jgss
distance-matrix-service_1  | root java.desktop jrt:/java.desktop
distance-matrix-service_1  | root java.naming jrt:/java.naming
distance-matrix-service_1  | root java.prefs jrt:/java.prefs
distance-matrix-service_1  | root java.security.sasl jrt:/java.security.sasl
distance-matrix-service_1  | root java.base jrt:/java.base
distance-matrix-service_1  | java.security.sasl requires java.logging jrt:/java.logging
distance-matrix-service_1  | java.prefs requires java.xml jrt:/java.xml
distance-matrix-service_1  | java.naming requires java.security.sasl jrt:/java.security.sasl
distance-matrix-service_1  | java.desktop requires java.datatransfer jrt:/java.datatransfer
distance-matrix-service_1  | java.desktop requires java.prefs jrt:/java.prefs
distance-matrix-service_1  | java.desktop requires java.xml jrt:/java.xml
distance-matrix-service_1  | java.security.jgss requires java.naming jrt:/java.naming
distance-matrix-service_1  | java.sql requires java.logging jrt:/java.logging
distance-matrix-service_1  | java.sql requires java.transaction.xa jrt:/java.transaction.xa
distance-matrix-service_1  | java.sql requires java.xml jrt:/java.xml
distance-matrix-service_1  | java.datatransfer binds java.desktop jrt:/java.desktop
distance-matrix-service_1  | java.base binds java.logging jrt:/java.logging
distance-matrix-service_1  | java.base binds java.management jrt:/java.management
distance-matrix-service_1  | java.base binds java.desktop jrt:/java.desktop
distance-matrix-service_1  | java.base binds java.naming jrt:/java.naming
distance-matrix-service_1  | java.base binds java.security.sasl jrt:/java.security.sasl
distance-matrix-service_1  | java.base binds java.security.jgss jrt:/java.security.jgss
distance-matrix-service_1  | VM settings:
distance-matrix-service_1  |     Max. Heap Size (Estimated): 2.93G
distance-matrix-service_1  |     Using VM: OpenJDK 64-Bit Server VM
distance-matrix-service_1  |
distance-matrix-service_1  | openjdk version "11-ea" 2018-09-25
distance-matrix-service_1  | OpenJDK Runtime Environment 18.9 (build 11-ea+25)
distance-matrix-service_1  | OpenJDK 64-Bit Server VM 18.9 (build 11-ea+25, mixed mode)
distance-matrix-service_1  |    _____                        _____          _
distance-matrix-service_1  |   / ____|                      / ____|        | |
distance-matrix-service_1  |  | (___  _   _ _ __   ___ _ __| |     __ _ ___| |__
distance-matrix-service_1  |   \___ \| | | | '_ \ / _ \ '__| |    / _` / __| '_ \
distance-matrix-service_1  |   ____) | |_| | |_) |  __/ |  | |___| (_| \__ \ | | |
distance-matrix-service_1  |  |_____/ \__,_| .__/ \___|_|   \_____\__,_|___/_| |_|
distance-matrix-service_1  |               | |              💰💰💰💰💰💰💰💰💰💰💰
distance-matrix-service_1  |               |_| :: SpringBoot :: 2.3.5.RELEASE ::
distance-matrix-service_1  |
distance-matrix-service_1  | 2020-11-06 15:46:15.612  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.s.p.service.DistanceMatrixApplication  : No active profile set, falling back to default profiles: default
distance-matrix-service_1  | 2020-11-06 15:46:15.702 DEBUG [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.b.c.c.ConfigFileApplicationListener  : Loaded config file 'jar:file:/runtime/server.jar!/BOOT-INF/classes!/application.yaml' (classpath:/application.yaml)
distance-matrix-service_1  | 2020-11-06 15:46:20.244  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.cloud.context.scope.GenericScope     : BeanFactory id=427d56cb-2336-346c-811c-b1f06e8b0493
distance-matrix-service_1  | 2020-11-06 15:46:22.412  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
distance-matrix-service_1  | 2020-11-06 15:46:22.460  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
distance-matrix-service_1  | 2020-11-06 15:46:22.460  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.39]
distance-matrix-service_1  | 2020-11-06 15:46:22.719  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
distance-matrix-service_1  | 2020-11-06 15:46:22.720  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 7017 ms
distance-matrix-service_1  | 2020-11-06 15:46:23.010  WARN [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] i.m.c.i.binder.jvm.JvmGcMetrics          : GC notifications will not be available because com.sun.management.GarbageCollectionNotificationInfo is not present
distance-matrix-service_1  | 2020-11-06 15:46:25.780  WARN [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.h.v.i.p.javabean.JavaBeanExecutable    : HV000254: Missing parameter metadata for TimeUnit(String, int, long), which declares implicit or synthetic parameters. Automatic resolution of generic type information for method parameters may yield incorrect results if multiple parameters have the same erasure. To solve this, compile your code with the '-parameters' flag.
distance-matrix-service_1  | 2020-11-06 15:46:26.128  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] anceMatrixGoogleGeoAPICachedProxyService : Bootstrapping the Google Geo API with token: AIza**********Ojo_
distance-matrix-service_1  | 2020-11-06 15:46:26.742  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] anceMatrixGoogleGeoAPICachedProxyService : Initialized Google Geo API
distance-matrix-service_1  | 2020-11-06 15:46:26.745  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] anceMatrixGoogleGeoAPICachedProxyService : Bootstrapping the Results Cache; eviction time of 5 MINUTES
distance-matrix-service_1  | 2020-11-06 15:46:26.808  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] anceMatrixGoogleGeoAPICachedProxyService : Initialized the Results Cache
distance-matrix-service_1  | 2020-11-06 15:46:28.041 DEBUG [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : 8 mappings in 'requestMappingHandlerMapping'
distance-matrix-service_1  | 2020-11-06 15:46:28.119  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 16 endpoint(s) beneath base path '/actuator'
distance-matrix-service_1  | 2020-11-06 15:46:28.709  WARN [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.n.c.sources.URLConfigurationSource     : No URLs will be polled as dynamic configuration sources.
distance-matrix-service_1  | 2020-11-06 15:46:28.710  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.n.c.sources.URLConfigurationSource     : To enable URLs as dynamic configuration sources, define System property archaius.configurationSource.additionalUrls or make config.properties available on classpath.
distance-matrix-service_1  | 2020-11-06 15:46:28.730  WARN [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.n.c.sources.URLConfigurationSource     : No URLs will be polled as dynamic configuration sources.
distance-matrix-service_1  | 2020-11-06 15:46:28.730  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.n.c.sources.URLConfigurationSource     : To enable URLs as dynamic configuration sources, define System property archaius.configurationSource.additionalUrls or make config.properties available on classpath.
distance-matrix-service_1  | 2020-11-06 15:46:29.272  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
distance-matrix-service_1  | 2020-11-06 15:46:29.351 DEBUG [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] s.w.s.m.m.a.RequestMappingHandlerAdapter : ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
distance-matrix-service_1  | 2020-11-06 15:46:29.686 DEBUG [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Patterns [/swagger-ui/] in 'viewControllerHandlerMapping'
distance-matrix-service_1  | 2020-11-06 15:46:29.750 DEBUG [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Patterns [/webjars/**, /**, /swagger-ui/**] in 'resourceHandlerMapping'
distance-matrix-service_1  | 2020-11-06 15:46:29.788 DEBUG [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] .m.m.a.ExceptionHandlerExceptionResolver : ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
distance-matrix-service_1  | 2020-11-06 15:46:31.017  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
distance-matrix-service_1  | 2020-11-06 15:46:31.996  INFO [app=distance-matrix-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.s.p.service.DistanceMatrixApplication  : Started DistanceMatrixApplication in 20.779 seconds (JVM running for 24.009)
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

* How to relate Logging, Tracing and Metrics from the service. Anyone can quickly verify any piece of the application requests.

## Logging

* The Access logs follows the patterns of the Apache access logs.
  * Starts with the token `[ACCESS]`
  * Contains information about the entire
* App logs are for internal details of the server
  * Includes app name, profile, trace, span, thread and message

```
2020-11-06 12:24:11.890 DEBUG [app=distance-matrix-service,profiles=default][trace_id=700537f9e3441ecb,span_id=700537f9e3441ecb,span_xprt=true][supercash_tid=123,supercash_cid=marcello] 83605 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
[ACCESS] - [06/Nov/2020:12:24:11 -0300] http_method=POST http_path=/v1/distancematrix http_query= http_protocol=HTTP/1.1 http_status=200 latency_total=584ms latency_commit=583ms response_size=43bytes - supercash_tid=123 supercash_cid=marcello x_b3_traceid=700537f9e3441ecb
```

* Configuration
  * Access logs: `application.yaml`, property `server.tomcat.accesslog.pattern`
  * App logs: `logback-spring.yaml`, property `CONSOLE_LOG_PATTERN`

## Tracing

* SpringBoot implements Sleuth client to collect metrics.
* Zipkin server is used to debug metrics of different services using traces, spans, etc.

### How to Zipkin

* Start the `tools/zipkin` docker-compose stack, open the URL http://localhost:9411/zipkin
* Start the this service and make the requests to start using tracing

After making requests, go to `Zipkin screen -> Find a Trace -> [+] -> serviceName=distance-matrix-service -> [Run Query]`

* It will show the traces for the requests
* Expand the traces, copy the value of `X-B3-TraceId`
  * Paste at the field `Search by trace ID` and press ENTER.

### Configuration
  * Sleuth Filter
  * `logback-spring.yaml`, property `CONSOLE_LOG_PATTERN`

### Trace ID on the Client 

The traces will show with the spans.

* In the client-side, you can see the `Trace ID` through the HTTP Response Header `X-Trace-ID`. The example as shown above.

```console
$ curl -i localhost:8080/v1/distancematrix \
    -H 'supercash_cid: marcello' \
    -H 'supercash_tid: 123' \
    -H 'content-type: application/json' \
    -d '{ "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" }'
HTTP/1.1 200
X-B3-TraceId: 3e635cc4b8771243
api-version: v1
supercash_tid: 123
supercash_cid: marcello
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 06 Nov 2020 14:19:18 GMT

{"distance":257055,"time":13519}%
```

### Trace ID on the server

* All the logs will display the traceID 
  * It is also included in the ACCESS logs

```
2020-11-06 12:24:11.890 DEBUG [app=distance-matrix-service,profiles=default][trace_id=700537f9e3441ecb,span_id=700537f9e3441ecb,span_xprt=true][supercash_tid=123,supercash_cid=marcello] 83605 --- [nio-8080-exec-1] o.s.w.s.m.m.a.HttpEntityMethodProcessor  : Writing [DistanceMatrixResult [distance=257055, time=13519]]
2020-11-06 12:24:11.890 DEBUG [app=distance-matrix-service,profiles=default][trace_id=700537f9e3441ecb,span_id=700537f9e3441ecb,span_xprt=true][supercash_tid=123,supercash_cid=marcello] 83605 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
[ACCESS] - [06/Nov/2020:12:24:11 -0300] http_method=POST http_path=/v1/distancematrix http_query= http_protocol=HTTP/1.1 http_status=200 latency_total=584ms latency_commit=583ms response_size=43bytes - supercash_tid=123 supercash_cid=marcello x_b3_traceid=700537f9e3441ecb
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
