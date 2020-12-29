# parking-plus-service

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
Successfully tagged supercash/parking-plus-service:latest
Creating parking-plus-service_parking-plus-service_1 ... done
Attaching to parking-plus-service_parking-plus-service_1
parking-plus-service_1  |
parking-plus-service_1  | => Initializing SpringBoot Runner 'start.sh'
parking-plus-service_1  |
parking-plus-service_1  | ########## Running init scripts at '/runtime/init'
parking-plus-service_1  |
parking-plus-service_1  |
parking-plus-service_1  | ########## Processing source hooks at '/runtime/sources'
parking-plus-service_1  |
parking-plus-service_1  | [1] source /runtime/sources/springboot.sh
parking-plus-service_1  |
parking-plus-service_1  | => Processing JAVA_OPTS hooks at /runtime/java-opts
parking-plus-service_1  |
parking-plus-service_1  | [1] JAVA_OPTS << ./java-opts/vminfo.opt
parking-plus-service_1  | [2] JAVA_OPTS << ./java-opts/docker.opt
parking-plus-service_1  | [3] JAVA_OPTS << ./java-opts/jdk-custom-debug.opt
parking-plus-service_1  | [4] JAVA_OPTS << ./java-opts/springboot.opt
parking-plus-service_1  |
parking-plus-service_1  | Exporting found opts JAVA_OPTS= -showversion -XshowSettings:vm -XX:+UnlockExperimentalVMOptions --show-module-resolution -Djava.security.egd=file:/dev/./urandom
parking-plus-service_1  |
parking-plus-service_1  | ####### Starting the app #######
parking-plus-service_1  |
parking-plus-service_1  | java $JAVA_OPTS -jar /runtime/server.jar $JAR_OPTS
parking-plus-service_1  |
parking-plus-service_1  | root java.sql jrt:/java.sql
parking-plus-service_1  | root java.logging jrt:/java.logging
parking-plus-service_1  | root java.transaction.xa jrt:/java.transaction.xa
parking-plus-service_1  | root java.management jrt:/java.management
parking-plus-service_1  | root java.xml jrt:/java.xml
parking-plus-service_1  | root jdk.unsupported jrt:/jdk.unsupported
parking-plus-service_1  | root java.datatransfer jrt:/java.datatransfer
parking-plus-service_1  | root java.instrument jrt:/java.instrument
parking-plus-service_1  | root java.security.jgss jrt:/java.security.jgss
parking-plus-service_1  | root java.desktop jrt:/java.desktop
parking-plus-service_1  | root java.naming jrt:/java.naming
parking-plus-service_1  | root java.prefs jrt:/java.prefs
parking-plus-service_1  | root java.security.sasl jrt:/java.security.sasl
parking-plus-service_1  | root java.base jrt:/java.base
parking-plus-service_1  | java.security.sasl requires java.logging jrt:/java.logging
parking-plus-service_1  | java.prefs requires java.xml jrt:/java.xml
parking-plus-service_1  | java.naming requires java.security.sasl jrt:/java.security.sasl
parking-plus-service_1  | java.desktop requires java.datatransfer jrt:/java.datatransfer
parking-plus-service_1  | java.desktop requires java.prefs jrt:/java.prefs
parking-plus-service_1  | java.desktop requires java.xml jrt:/java.xml
parking-plus-service_1  | java.security.jgss requires java.naming jrt:/java.naming
parking-plus-service_1  | java.sql requires java.logging jrt:/java.logging
parking-plus-service_1  | java.sql requires java.transaction.xa jrt:/java.transaction.xa
parking-plus-service_1  | java.sql requires java.xml jrt:/java.xml
parking-plus-service_1  | java.datatransfer binds java.desktop jrt:/java.desktop
parking-plus-service_1  | java.base binds java.logging jrt:/java.logging
parking-plus-service_1  | java.base binds java.management jrt:/java.management
parking-plus-service_1  | java.base binds java.desktop jrt:/java.desktop
parking-plus-service_1  | java.base binds java.naming jrt:/java.naming
parking-plus-service_1  | java.base binds java.security.sasl jrt:/java.security.sasl
parking-plus-service_1  | java.base binds java.security.jgss jrt:/java.security.jgss
parking-plus-service_1  | VM settings:
parking-plus-service_1  |     Max. Heap Size (Estimated): 2.93G
parking-plus-service_1  |     Using VM: OpenJDK 64-Bit Server VM
parking-plus-service_1  |
parking-plus-service_1  | openjdk version "11-ea" 2018-09-25
parking-plus-service_1  | OpenJDK Runtime Environment 18.9 (build 11-ea+25)
parking-plus-service_1  | OpenJDK 64-Bit Server VM 18.9 (build 11-ea+25, mixed mode)
parking-plus-service_1  |    _____                        _____          _
parking-plus-service_1  |   / ____|                      / ____|        | |
parking-plus-service_1  |  | (___  _   _ _ __   ___ _ __| |     __ _ ___| |__
parking-plus-service_1  |   \___ \| | | | '_ \ / _ \ '__| |    / _` / __| '_ \
parking-plus-service_1  |   ____) | |_| | |_) |  __/ |  | |___| (_| \__ \ | | |
parking-plus-service_1  |  |_____/ \__,_| .__/ \___|_|   \_____\__,_|___/_| |_|
parking-plus-service_1  |               | |              💰💰💰💰💰💰💰💰💰💰💰
parking-plus-service_1  |               |_| :: SpringBoot :: 2.3.5.RELEASE ::
parking-plus-service_1  |
parking-plus-service_1  | 2020-11-06 15:46:15.612  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.s.p.service.DistanceMatrixApplication  : No active profile set, falling back to default profiles: default
parking-plus-service_1  | 2020-11-06 15:46:15.702 DEBUG [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.b.c.c.ConfigFileApplicationListener  : Loaded config file 'jar:file:/runtime/server.jar!/BOOT-INF/classes!/application.yaml' (classpath:/application.yaml)
parking-plus-service_1  | 2020-11-06 15:46:20.244  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.cloud.context.scope.GenericScope     : BeanFactory id=427d56cb-2336-346c-811c-b1f06e8b0493
parking-plus-service_1  | 2020-11-06 15:46:22.412  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
parking-plus-service_1  | 2020-11-06 15:46:22.460  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
parking-plus-service_1  | 2020-11-06 15:46:22.460  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.39]
parking-plus-service_1  | 2020-11-06 15:46:22.719  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
parking-plus-service_1  | 2020-11-06 15:46:22.720  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 7017 ms
parking-plus-service_1  | 2020-11-06 15:46:23.010  WARN [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] i.m.c.i.binder.jvm.JvmGcMetrics          : GC notifications will not be available because com.sun.management.GarbageCollectionNotificationInfo is not present
parking-plus-service_1  | 2020-11-06 15:46:25.780  WARN [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.h.v.i.p.javabean.JavaBeanExecutable    : HV000254: Missing parameter metadata for TimeUnit(String, int, long), which declares implicit or synthetic parameters. Automatic resolution of generic type information for method parameters may yield incorrect results if multiple parameters have the same erasure. To solve this, compile your code with the '-parameters' flag.
parking-plus-service_1  | 2020-11-06 15:46:26.128  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] anceMatrixGoogleGeoAPICachedProxyService : Bootstrapping the Google Geo API with token: AIza**********Ojo_
parking-plus-service_1  | 2020-11-06 15:46:26.742  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] anceMatrixGoogleGeoAPICachedProxyService : Initialized Google Geo API
parking-plus-service_1  | 2020-11-06 15:46:26.745  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] anceMatrixGoogleGeoAPICachedProxyService : Bootstrapping the Results Cache; eviction time of 5 MINUTES
parking-plus-service_1  | 2020-11-06 15:46:26.808  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] anceMatrixGoogleGeoAPICachedProxyService : Initialized the Results Cache
parking-plus-service_1  | 2020-11-06 15:46:28.041 DEBUG [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : 8 mappings in 'requestMappingHandlerMapping'
parking-plus-service_1  | 2020-11-06 15:46:28.119  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 16 endpoint(s) beneath base path '/actuator'
parking-plus-service_1  | 2020-11-06 15:46:28.709  WARN [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.n.c.sources.URLConfigurationSource     : No URLs will be polled as dynamic configuration sources.
parking-plus-service_1  | 2020-11-06 15:46:28.710  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.n.c.sources.URLConfigurationSource     : To enable URLs as dynamic configuration sources, define System property archaius.configurationSource.additionalUrls or make config.properties available on classpath.
parking-plus-service_1  | 2020-11-06 15:46:28.730  WARN [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.n.c.sources.URLConfigurationSource     : No URLs will be polled as dynamic configuration sources.
parking-plus-service_1  | 2020-11-06 15:46:28.730  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.n.c.sources.URLConfigurationSource     : To enable URLs as dynamic configuration sources, define System property archaius.configurationSource.additionalUrls or make config.properties available on classpath.
parking-plus-service_1  | 2020-11-06 15:46:29.272  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
parking-plus-service_1  | 2020-11-06 15:46:29.351 DEBUG [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] s.w.s.m.m.a.RequestMappingHandlerAdapter : ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
parking-plus-service_1  | 2020-11-06 15:46:29.686 DEBUG [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Patterns [/swagger-ui/] in 'viewControllerHandlerMapping'
parking-plus-service_1  | 2020-11-06 15:46:29.750 DEBUG [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Patterns [/webjars/**, /**, /swagger-ui/**] in 'resourceHandlerMapping'
parking-plus-service_1  | 2020-11-06 15:46:29.788 DEBUG [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] .m.m.a.ExceptionHandlerExceptionResolver : ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
parking-plus-service_1  | 2020-11-06 15:46:31.017  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
parking-plus-service_1  | 2020-11-06 15:46:31.996  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 16 --- [           main] c.s.p.service.DistanceMatrixApplication  : Started DistanceMatrixApplication in 20.779 seconds (JVM running for 24.009)
```

# API

* Go to the Swagger URL to discover the APIs available
  * http://localhost:8082/swagger-ui/
  * Keep the end `/` or else it fails to open
* The OpenAPI endpoint for discovery is `/swagger/docs/v2`
  * This is to support Gloo API Gateway (https://docs.solo.io/gloo/latest/installation/advanced_configuration/fds_mode/)
* Stubs: located at https://gitlab.com/supercash/clients/distance-matrix-client-resttemplate
  * Generated by https://github.com/marcellodesales/swagger-client-package-repo-gen

## API Flows

The API Flow is as follows:

* Clients must fetch the current Supercash sales: `/v1/parking_lots/sales`
  * A sale has an ID and must be always submitted
* Get the ticket status with the mapped sale ID: `/v1/parking_lots/tickets/status`
  * Getting the status of the sale just in case
* Authorize a payment based on the values: /v1/parking_lots/payments/authorize`
  * Adjust the price value to 0 and the paid with the value
  * Using the sale ID as well
* Get the status of the payments: `/v1/parking_lots/payments/status`
  * Verify if the payment went through

## API `/v1/parking_lots/sales`

* Just needs to have the `tid` and `uid`

```console
curl http://localhost:8080/v1/parking_lots/sales
     -H "accept: application/json"
     -H "supercash_tid: 2323"
     -H "supercash_uid: 12123"
```

* Response

```json
{
  "current": [
    {
      "bandeira": null,
      "descricao": "PROMOCAO SUPERCASH PROMO 1",
      "diasSemana": [
        "QUINTA",
        "DOMINGO",
        "SABADO",
        "SEXTA",
        "TERCA",
        "QUARTA",
        "SEGUNDA"
      ],
      "exigeAutenticacao": false,
      "horarioFim": "1970-01-02T02:59:00.000Z",
      "horarioInicio": "1970-01-01T03:00:00.000Z",
      "imagem": null,
      "nome": "SUPERCASH 1",
      "quantidadeDisponivel": null,
      "regulamento": null,
      "systemId": 15,
      "tipoDesconto": "VALOR",
      "tipoPromocao": "CUPOM",
      "titulo": "SUPERCASH PROMO 1",
      "validade": "2027-11-10T02:00:00.000Z",
      "valorAlvo": 0,
      "valorDesconto": 100
    },
    {
      "bandeira": null,
      "descricao": "SUPERCASH PROMO 7",
      "diasSemana": [
        "QUINTA",
        "DOMINGO",
        "SABADO",
        "SEXTA",
        "TERCA",
        "QUARTA",
        "SEGUNDA"
      ],
      "exigeAutenticacao": false,
      "horarioFim": "1970-01-02T02:59:00.000Z",
      "horarioInicio": "1970-01-01T03:00:00.000Z",
      "imagem": null,
      "nome": "SUPERCASH 7",
      "quantidadeDisponivel": null,
      "regulamento": null,
      "systemId": 21,
      "tipoDesconto": "VALOR",
      "tipoPromocao": "CUPOM",
      "titulo": "SUPERCASH PROMO 7",
      "validade": "2028-11-10T02:00:00.000Z",
      "valorAlvo": 0,
      "valorDesconto": 700
    },
    {
      "bandeira": null,
      "descricao": "SUPERCASH PROMO 6",
      "diasSemana": [
        "QUINTA",
        "DOMINGO",
        "SABADO",
        "SEXTA",
        "TERCA",
        "QUARTA",
        "SEGUNDA"
      ],
      "exigeAutenticacao": false,
      "horarioFim": "1970-01-02T02:59:00.000Z",
      "horarioInicio": "1970-01-01T03:00:00.000Z",
      "imagem": null,
      "nome": "SUPERCASH 6",
      "quantidadeDisponivel": null,
      "regulamento": null,
      "systemId": 20,
      "tipoDesconto": "VALOR",
      "tipoPromocao": "CUPOM",
      "titulo": "SUPERCASH PROMO 6",
      "validade": "2029-11-10T02:00:00.000Z",
      "valorAlvo": 0,
      "valorDesconto": 600
    },
    {
      "bandeira": null,
      "descricao": "SUPERCASH PROMO 3",
      "diasSemana": [
        "QUINTA",
        "DOMINGO",
        "SABADO",
        "SEXTA",
        "TERCA",
        "QUARTA",
        "SEGUNDA"
      ],
      "exigeAutenticacao": false,
      "horarioFim": "1970-01-02T02:59:00.000Z",
      "horarioInicio": "1970-01-01T03:00:00.000Z",
      "imagem": null,
      "nome": "SUPERCASH 3",
      "quantidadeDisponivel": null,
      "regulamento": null,
      "systemId": 17,
      "tipoDesconto": "VALOR",
      "tipoPromocao": "CUPOM",
      "titulo": "SUPERCASH PROMO 3",
      "validade": "2027-11-10T02:00:00.000Z",
      "valorAlvo": 3,
      "valorDesconto": 300
    },
    {
      "bandeira": null,
      "descricao": "SUPERCASH PROMO 2",
      "diasSemana": [
        "QUINTA",
        "DOMINGO",
        "SABADO",
        "SEXTA",
        "TERCA",
        "QUARTA",
        "SEGUNDA"
      ],
      "exigeAutenticacao": false,
      "horarioFim": "1970-01-02T02:59:00.000Z",
      "horarioInicio": "1970-01-01T03:00:00.000Z",
      "imagem": null,
      "nome": "SUPERCASH 2",
      "quantidadeDisponivel": null,
      "regulamento": null,
      "systemId": 16,
      "tipoDesconto": "VALOR",
      "tipoPromocao": "CUPOM",
      "titulo": "SUPERCASH PROMO 2",
      "validade": "2027-11-10T02:00:00.000Z",
      "valorAlvo": 0,
      "valorDesconto": 200
    },
    {
      "bandeira": null,
      "descricao": "SUPERCASH PROMO 4",
      "diasSemana": [
        "QUINTA",
        "DOMINGO",
        "SABADO",
        "SEXTA",
        "TERCA",
        "QUARTA",
        "SEGUNDA"
      ],
      "exigeAutenticacao": false,
      "horarioFim": "1970-01-02T02:59:00.000Z",
      "horarioInicio": "1970-01-01T03:00:00.000Z",
      "imagem": null,
      "nome": "SUPERCASH 4",
      "quantidadeDisponivel": null,
      "regulamento": null,
      "systemId": 18,
      "tipoDesconto": "VALOR",
      "tipoPromocao": "CUPOM",
      "titulo": "SUPERCASH PROMO 4",
      "validade": "2028-11-10T02:00:00.000Z",
      "valorAlvo": 0,
      "valorDesconto": 400
    },
    {
      "bandeira": null,
      "descricao": "SUPERCASH PROMO 5",
      "diasSemana": [
        "QUINTA",
        "DOMINGO",
        "SABADO",
        "SEXTA",
        "TERCA",
        "QUARTA",
        "SEGUNDA"
      ],
      "exigeAutenticacao": false,
      "horarioFim": "1970-01-02T02:59:00.000Z",
      "horarioInicio": "1970-01-01T03:00:00.000Z",
      "imagem": null,
      "nome": "SUPERCASH 5",
      "quantidadeDisponivel": null,
      "regulamento": null,
      "systemId": 19,
      "tipoDesconto": "VALOR",
      "tipoPromocao": "CUPOM",
      "titulo": "SUPERCASH PROMO 5",
      "validade": "2027-11-10T02:00:00.000Z",
      "valorAlvo": 0,
      "valorDesconto": 500
    }
  ]
}
```

## API `/v1/parking_lots/tickets/status`

> **NOTE**: The test site is **not** being validated.

| Method | Endpoint | Headers | Payload |
| ----- | ----- | ---- | ----- |
| POST  | /v1/parking_lots/tickets/status | `content-type: application/json` | "{ \"ticketNumber\": \"029466845168\", \"userId\": \"dokdoskdoskdoskd\"}" |

* Response

| Headers | Payload |
| -- | -- |
| `Content-Type: application/json` | {"distance":257055,"time":13516} |

## API `/v1/parking_lots/tickets/status`

* Tickets Status: Verify if the ticket has been paid.
  * Verify the values of `"tarifa":13500,"tarifaPaga":0` if it has been paid.

```console
$ curl -i "http://localhost:8080/v1/parking_lots/tickets/status" \
    -H "supercash_cid: 883838383" 
    -H "supercash_tid: cxxxoooxso334" 
    -H "Content-Type: application/json" 
    -d "{ \"ticketNumber\": \"029466845168\", \"userId\": \"dokdoskdoskdoskd\"}"
HTTP/1.1 200
X-B3-TraceId: a34f2a5d17785a36
api-version: v1
supercash_tid: cxxxoooxso334
supercash_cid: 883838383
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 07 Nov 2020 23:00:43 GMT

{"status":{"cnpjGaragem":"14.207.662/0001-41","dataConsulta":1604790043167,
           "dataDeEntrada":1604080595000,"dataPermitidaSaida":1604804160000,
           "dataPermitidaSaidaUltimoPagamento":null,"errorCode":0,"garagem":"GARAGEM A",
           "idGaragem":1,"idPromocao":null,"imagemLink":null,"mensagem":"","notas":[],
           "numeroTicket":"029466845168","promocaoAtingida":false,"promocoesDisponiveis":true,
           "setor":"ESTACIONAMENTO","tarifa":13500,"tarifaPaga":0,"tarifaSemDesconto":13500,
           "ticketValido":true,"valorDesconto":0}}%
```

## Getting the status of a ticket

## Updating the ticket to paid

* Just get the value of the ticket and set it to 0

```console
curl -X POST "http://localhost:8080/v1/parking_lots/tickets/status" -H "accept: application/json" -H "supercash_cid: 883838383" -H "supercash_tid: cxxxoooxso334" -H "Content-Type: application/json" -d "{ \"ticketNumber\": \"029466845168\", \"userId\": \"dokdoskdoskdoskd\"}"

{
  "status": {
    "cnpjGaragem": "14.207.662/0001-41",
    "dataConsulta": 1604792329181,
    "dataDeEntrada": 1604080595000,
    "dataPermitidaSaida": 1604793316883,
    "dataPermitidaSaidaUltimoPagamento": 1604793316883,
    "errorCode": 0,
    "garagem": "GARAGEM A",
    "idGaragem": 1,
    "idPromocao": null,
    "imagemLink": null,
    "mensagem": "",
    "notas": [],
    "numeroTicket": "029466845168",
    "promocaoAtingida": false,
    "promocoesDisponiveis": true,
    "setor": "ESTACIONAMENTO",
    "tarifa": 0,
    "tarifaPaga": 13500,
    "tarifaSemDesconto": 0,
    "ticketValido": true,
    "valorDesconto": 0
  }
}
```

> **NOTE**: Repeating the same operation should fail, as expected.

```console
$ curl -i  "http://localhost:8080/v1/parking_lots/payments/authorize" 
  -H "supercash_cid: super-customer" \
  -H "supercash_tid: super-transc" \
  -H "Content-Type: application/json" \
  -d "{ \"request\": { \"bandeira\": \"noite\", \"faturado\": true, \"idGaragem\": 1, 
        \"idTransacao\": \"supercash-in-app-pay\", \"numeroTicket\": \"029466845168\", 
        \"permitirValorExcedente\": true, \"permitirValorParcial\": true, 
        \"tokenAutenticacao\": \"string\", \"udid\": \"user-oaoao\", \"valor\": 13500 }}"
HTTP/1.1 500
X-B3-TraceId: 18ae5913e4df9813
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 08 Nov 2020 02:19:10 GMT
Connection: close

{"description":"[403] during [POST] to 
  [https://demonstracao.parkingplus.com.br/servicos/2/pagamentoAutorizado?apiKey=38520a90f721d3d1c68058fe4885f4eaa
       1937fa8&apiKeyId=1] [ServicoPagamentoTicket2Api#pagarTicketAutorizadoUsingPOST(String,PagamentoAutorizadoRequ
       est,Long)]: [{\"mensagem\":\"Transação já realizada.\",\"errorCode\":9}]","error":500}%
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
  * https://gitlab.com/supercash/services/parking-plus-service/-/settings/ci_cd#js-general-pipeline-settings

```
$ gradle check

Coverage summary:
parking-plus-service:   87.4%

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
2020-11-06 12:24:11.890 DEBUG [app=parking-plus-service,profiles=default][trace_id=700537f9e3441ecb,span_id=700537f9e3441ecb,span_xprt=true][supercash_tid=123,supercash_cid=marcello] 83605 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
[ACCESS] - [06/Nov/2020:12:24:11 -0300] http_method=POST http_path=/v1/parkingplus http_query= http_protocol=HTTP/1.1 http_status=200 latency_total=584ms latency_commit=583ms response_size=43bytes - supercash_tid=123 supercash_cid=marcello x_b3_traceid=700537f9e3441ecb
```

* Configuration
  * Access logs: `application.yaml`, property `server.tomcat.accesslog.pattern`
  * App logs: `logback-spring.yaml`, property `CONSOLE_LOG_PATTERN`

* Feign Clients Logs the requests to other services:
  * Change the property of the cash.super
  * https://github.com/swagger-api/swagger-codegen/issues/10550

```console
2020-11-08 11:14:47.087 DEBUG [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 7962 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
2020-11-08 11:14:47.087  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 7962 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 6 ms
2020-11-08 11:14:47.087 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : POST "/v1/parking_lots/tickets/status", parameters={}
2020-11-08 11:14:47.088 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to cash.super_.platform.service.parkingplus.ParkingPlusProxyController#getTicketStatus(ParkingTicket, Optional, Optional)
2020-11-08 11:14:47.088 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] m.m.a.RequestResponseBodyMethodProcessor : Read "application/json;charset=UTF-8" to [ParkingTicket [userId=dokdoskdoskdoskd, ticketNumber=029466845168]]
2020-11-08 11:14:47.089  INFO [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] c.s.p.s.p.AbstractParkingLotProxyService : Requesting parking plus ticket status: ParkingTicket [userId=dokdoskdoskdoskd, ticketNumber=029466845168]
2020-11-08 11:14:47.091 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] ---> POST https://demonstracao.parkingplus.com.br/servicos/2/ticket?apiKey=acfb42c104c4bf15b5a6bf2844f25dca7b936d6f&apiKeyId=1 HTTP/1.1
2020-11-08 11:14:47.091 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] Accept: application/json
2020-11-08 11:14:47.091 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] Content-Length: 167
2020-11-08 11:14:47.091 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] Content-Type: application/json
2020-11-08 11:14:47.091 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] ---> END HTTP (167-byte body)
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] <--- HTTP/1.1 404 (1459ms)
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] connection: keep-alive
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] content-type: application/json;charset=UTF-8
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] date: Sun, 08 Nov 2020 14:14:48 GMT
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] p3p: CP="IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT"
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] server: nginx/1.18.0
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] strict-transport-security: max-age=31536000
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] transfer-encoding: chunked
2020-11-08 11:14:48.550 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=416263335ad8814b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] feign.Logger                             : [ServicoPagamentoTicket2Api#getTicketUsingPOST] <--- END HTTP (63-byte body)
2020-11-08 11:14:48.551 ERROR [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] c.s.p.s.p.AbstractParkingLotProxyService : Couldn't get the status of ticket: [404] during [POST] to [https://demonstracao.parkingplus.com.br/servicos/2/ticket?apiKey=acfb42c104c4bf15b5a6bf2844f25dca7b936d6f&apiKeyId=1] [ServicoPagamentoTicket2Api#getTicketUsingPOST(String,TicketRequest,Long)]: [{"mensagem":"Serviço indisponível no momento.","errorCode":2}]
2020-11-08 11:14:48.551 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] .m.m.a.ExceptionHandlerExceptionResolver : Using @ExceptionHandler cash.super_.platform.service.parkingplus.ParkingPlusProxyController#handleAllExceptions(Exception, WebRequest)
2020-11-08 11:14:48.552 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] o.s.w.s.m.m.a.HttpEntityMethodProcessor  : Using 'application/json', given [application/json] and supported [application/json, application/*+json, application/json, application/*+json]
2020-11-08 11:14:48.552 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] o.s.w.s.m.m.a.HttpEntityMethodProcessor  : Writing [{description=[404] during [POST] to [https://demonstracao.parkingplus.com.br/servicos/2/ticket?apiKe (truncated)...]
2020-11-08 11:14:48.553  WARN [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] .m.m.a.ExceptionHandlerExceptionResolver : Resolved [feign.FeignException$NotFound: [404] during [POST] to [https://demonstracao.parkingplus.com.br/servicos/2/ticket?apiKey=acfb42c104c4bf15b5a6bf2844f25dca7b936d6f&apiKeyId=1] [ServicoPagamentoTicket2Api#getTicketUsingPOST(String,TicketRequest,Long)]: [{"mensagem":"Serviço indisponível no momento.","errorCode":2}]]
2020-11-08 11:14:48.553 DEBUG [app=parking-plus-service,profiles=default][trace_id=50915ed9a005420b,span_id=50915ed9a005420b,span_xprt=true][supercash_tid=cxxxoooxso334,supercash_cid=883838383] 7962 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 500 INTERNAL_SERVER_ERROR
[ACCESS] 0:0:0:0:0:0:0:1 [08/Nov/2020:11:14:48 -0300] - http_method=POST http_path=/v1/parking_lots/tickets/status http_query= http_protocol=HTTP/1.1 http_status=500 latency_total=1472ms latency_commit=1471ms response_size=331bytes - - supercash_tid=cxxxoooxso334 supercash_cid=883838383 x_b3_traceid=50915ed9a005420b
2020-11-08 11:15:11.616  INFO [app=parking-plus-service,profiles=default][trace_id=,span_id=,span_xprt=][supercash_tid=,supercash_cid=] 7962 --- [      Thread-88] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
```

## Tracing

* SpringBoot implements Sleuth client to collect metrics.
* Zipkin server is used to debug metrics of different services using traces, spans, etc.

### How to Zipkin

* Start the `tools/zipkin` docker-compose stack, open the URL http://localhost:9411/zipkin
* Start the this service and make the requests to start using tracing

After making requests, go to `Zipkin screen -> Find a Trace -> [+] -> serviceName=parking-plus-service -> [Run Query]`

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
$ curl -i localhost:8080/v1/parkingplus \
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
2020-11-06 12:24:11.890 DEBUG [app=parking-plus-service,profiles=default][trace_id=700537f9e3441ecb,span_id=700537f9e3441ecb,span_xprt=true][supercash_tid=123,supercash_cid=marcello] 83605 --- [nio-8080-exec-1] o.s.w.s.m.m.a.HttpEntityMethodProcessor  : Writing [DistanceMatrixResult [distance=257055, time=13519]]
2020-11-06 12:24:11.890 DEBUG [app=parking-plus-service,profiles=default][trace_id=700537f9e3441ecb,span_id=700537f9e3441ecb,span_xprt=true][supercash_tid=123,supercash_cid=marcello] 83605 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
[ACCESS] - [06/Nov/2020:12:24:11 -0300] http_method=POST http_path=/v1/parkingplus http_query= http_protocol=HTTP/1.1 http_status=200 latency_total=584ms latency_commit=583ms response_size=43bytes - supercash_tid=123 supercash_cid=marcello x_b3_traceid=700537f9e3441ecb
```

# Troubleshooting

* Rotate Google Maps token when getting errors about the token
* https://developers.google.com/maps/gmp-get-started

```console
$ curl -i localhost:8082/v1/parkingplus -H 'content-type: application/json' -d '{ "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" }'
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sun, 01 Nov 2020 14:23:56 GMT

{"description":"You must enable Billing on the Google Cloud Project at https://console.cloud.google.com/project/_/billing/enable Learn more at https://developers.google.com/maps/gmp-get-started","error":500}%
```
