# distance-matrix-service

Microservice to calculate distance and time.

# Setup

* See in `docker-compose.yaml` the needed environment variables

## Build

* Use docker-compose to bootstrap it locally

```console
$ docker-compose build
$ docker-compose build
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
5.0.0-jdk8-alpine-0.5.0: Pulling from intuit/unmazedboot-builder-gradle
4fe2ade4980c: Already exists
6fc58a8d4ae4: Already exists
ef87ded15917: Already exists
00094aa23f9e: Pull complete
1c48402e22e5: Pull complete
8d3a803b458b: Pull complete
1ca0a8473e3a: Pull complete
Digest: sha256:fe95f334228282b4f32bdc269eb59f1ea4e3c54ae458cae6f50ffec51401fd55
Status: Downloaded newer image for intuit/unmazedboot-builder-gradle:5.0.0-jdk8-alpine-0.5.0
# Executing 18 build triggers
 ---> Running in b6dcec715292
Removing intermediate container b6dcec715292
 ---> Running in 176f769905d5
installing-no-builder-dependencies
Removing intermediate container 176f769905d5
 ---> Running in 63328c174fe4
Removing intermediate container 63328c174fe4
 ---> Running in e9aae3231755
Removing intermediate container e9aae3231755
 ---> Running in 6e347c8b3f90
Removing intermediate container 6e347c8b3f90
 ---> Running in 9d0400a6fba0
Removing intermediate container 9d0400a6fba0
 ---> Running in 8a424207278a
Removing intermediate container 8a424207278a
 ---> Running in 1183834be801
Executing UNMAZEDBOOT_BUILDER_GRADLE_BUILD_CMD='gradle build -x test'
Removing intermediate container 1183834be801
 ---> Running in ed0635f9c63e

Welcome to Gradle 5.0!

Here are the highlights of this release:
 - Kotlin DSL 1.0
 - Task timeouts
 - Dependency alignment aka BOM support
 - Interactive `gradle init`

For more details see https://docs.gradle.org/5.0/release-notes.html

Starting a Gradle Daemon (subsequent builds will be faster)
> Task :compileJava
> Task :processResources
> Task :classes
> Task :bootJar
> Task :jar SKIPPED
> Task :assemble
> Task :check
> Task :build

BUILD SUCCESSFUL in 29s
3 actionable tasks: 3 executed
Removing intermediate container ed0635f9c63e
 ---> Running in 2fb839cc1507
Built artifacts at /app/build/libs and looking for package .jar
Removing intermediate container 2fb839cc1507
 ---> Running in 1c40233ebced
Removing intermediate container 1c40233ebced
 ---> Running in 8656bfd90031
Renaming the executable jar to the runtime dir
Removing intermediate container 8656bfd90031
 ---> Running in 18426f333b06
Removing intermediate container 18426f333b06
 ---> Running in 13c0803f797b
Contents for built /runtime/server.jar
Removing intermediate container 13c0803f797b
 ---> Running in 9bde3b12a53f
jar -tf /runtime/server.jar
Removing intermediate container 9bde3b12a53f
 ---> ffcf918083f6

Step 11/15 : FROM intuit/unmazedboot-linker:${UNMAZEDBOOT_LINKER_VERSION} as unmazedboot-jdk-linker
# Executing 7 build triggers
 ---> Running in 8b11a98dd495
Removing intermediate container 8b11a98dd495
 ---> Running in 60a4c861aabe
Removing intermediate container 60a4c861aabe
 ---> Running in 3016b05947a2
Building custom JRE '/opt/jdk-custom' with UNMAZEDBOOT_LINKER_JDK_MODULES='java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument'
Removing intermediate container 3016b05947a2
 ---> Running in 33d4a1efe2c5
Removing intermediate container 33d4a1efe2c5
 ---> Running in db52f159c61e
Linking jlink --module-path /opt/jdk/jmods     --verbose     --add-modules java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument     --output /opt/jdk-custom     --compress 2     --no-header-files     --no-man-pages || date --module-path /opt/jdk/jmods     --verbose     --add-modules java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument     --output /opt/jdk-custom     --compress 2     --no-header-files     --no-man-pages
Removing intermediate container db52f159c61e
 ---> Running in 04b775076fcb
java.base file:///opt/jdk/jmods/java.base.jmod
java.datatransfer file:///opt/jdk/jmods/java.datatransfer.jmod
java.desktop file:///opt/jdk/jmods/java.desktop.jmod
java.instrument file:///opt/jdk/jmods/java.instrument.jmod
java.logging file:///opt/jdk/jmods/java.logging.jmod
java.management file:///opt/jdk/jmods/java.management.jmod
java.naming file:///opt/jdk/jmods/java.naming.jmod
java.prefs file:///opt/jdk/jmods/java.prefs.jmod
java.security.jgss file:///opt/jdk/jmods/java.security.jgss.jmod
java.security.sasl file:///opt/jdk/jmods/java.security.sasl.jmod
java.sql file:///opt/jdk/jmods/java.sql.jmod
java.transaction.xa file:///opt/jdk/jmods/java.transaction.xa.jmod
java.xml file:///opt/jdk/jmods/java.xml.jmod
jdk.unsupported file:///opt/jdk/jmods/jdk.unsupported.jmod

Providers:
  java.desktop provides java.net.ContentHandlerFactory used by java.base
  java.base provides java.nio.file.spi.FileSystemProvider used by java.base
  java.naming provides java.security.Provider used by java.base
  java.security.jgss provides java.security.Provider used by java.base
  java.security.sasl provides java.security.Provider used by java.base
  java.desktop provides javax.print.PrintServiceLookup used by java.desktop
  java.desktop provides javax.print.StreamPrintServiceFactory used by java.desktop
  java.management provides javax.security.auth.spi.LoginModule used by java.base
  java.desktop provides javax.sound.midi.spi.MidiDeviceProvider used by java.desktop
  java.desktop provides javax.sound.midi.spi.MidiFileReader used by java.desktop
  java.desktop provides javax.sound.midi.spi.MidiFileWriter used by java.desktop
  java.desktop provides javax.sound.midi.spi.SoundbankReader used by java.desktop
  java.desktop provides javax.sound.sampled.spi.AudioFileReader used by java.desktop
  java.desktop provides javax.sound.sampled.spi.AudioFileWriter used by java.desktop
  java.desktop provides javax.sound.sampled.spi.FormatConversionProvider used by java.desktop
  java.desktop provides javax.sound.sampled.spi.MixerProvider used by java.desktop
  java.logging provides jdk.internal.logger.DefaultLoggerFinder used by java.base
  java.desktop provides sun.datatransfer.DesktopDatatransferService used by java.datatransfer
Removing intermediate container 04b775076fcb
 ---> Running in b5ad0366e6ce
Sun Nov  1 14:11:43 UTC 2020
Removing intermediate container b5ad0366e6ce
 ---> efb7dbdd3065

Step 12/15 : FROM intuit/unmazedboot-runner:${UNMAZEDBOOT_RUNNER_VERSION}
# Executing 39 build triggers
 ---> Using cache
 ---> Running in 8699188e95cc
Removing intermediate container 8699188e95cc
 ---> Running in 397763b3ce18
Removing intermediate container 397763b3ce18
 ---> Running in 9fb49312033c
Removing intermediate container 9fb49312033c
 ---> Running in cfe7bc3b265b
Removing intermediate container cfe7bc3b265b
 ---> Running in eebd900c06e4
Removing intermediate container eebd900c06e4
 ---> Running in 0297aa0938c2
Removing intermediate container 0297aa0938c2
 ---> Running in d07656cc1daf
Sources hooks dir during build
total 12
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 .
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 ..
-rw-r--r--    1 root     root           122 Nov  1 14:11 springboot.sh
Removing intermediate container d07656cc1daf
 ---> Running in 0fe34a04d41e
Removing intermediate container 0fe34a04d41e
 ---> Running in ebcc7616a4e9
Removing intermediate container ebcc7616a4e9
 ---> Running in bef834b0a1d0
Creating java-opts hooks dir /runtime/java-opts for JAVA_OPTS creation
Removing intermediate container bef834b0a1d0
 ---> Running in 2ac385e76c66
Removing intermediate container 2ac385e76c66
 ---> Running in 6f8e91191504
Removing intermediate container 6f8e91191504
 ---> Running in 549a4d406cdc
Removing intermediate container 549a4d406cdc
 ---> Running in b59ce70b4340
Removing intermediate container b59ce70b4340
 ---> Running in 66d058dcb70a
Removing intermediate container 66d058dcb70a
 ---> Running in 4401467254fd
JAVA_OPTS hooks during build
total 24
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 .
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 ..
-rw-r--r--    1 root     root            33 Nov  1 14:11 docker.opt
-rw-r--r--    1 root     root            25 Nov  1 14:11 jdk-custom-debug.opt
-rw-r--r--    1 root     root            40 Nov  1 14:11 springboot.opt
-rw-r--r--    1 root     root            31 Nov  1 14:11 vminfo.opt
Removing intermediate container 4401467254fd
 ---> Running in b95e8b68a2f4
Removing intermediate container b95e8b68a2f4
 ---> Running in 53b535f2db43
Removing intermediate container 53b535f2db43
 ---> Running in 1c9b553ceb8c
Creating init scripts dir /runtime/init to execute before the app
Removing intermediate container 1c9b553ceb8c
 ---> Running in 142ecce188ca
Removing intermediate container 142ecce188ca
 ---> Running in a54590f538e7
Will copy from unmazedboot-builder-artifacts /runtime /runtime
Removing intermediate container a54590f538e7
 ---> Running in f3fa868438f5
total 17320
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 .
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 ..
-rw-r--r--    1 root     root          1796 Dec  9  2018 collate.bash
drwxr-xr-x    2 root     root          4096 Nov  1 14:11 init
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 java-opts
-rw-r--r--    1 root     root      17699673 Nov  1 14:11 server.jar
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 sources
-rw-r--r--    1 root     root          4446 Dec  9  2018 start.bash
Removing intermediate container f3fa868438f5
 ---> Running in 9b18df9322ee
Will copy from /app/src/main/resources /runtime/resources
Removing intermediate container 9b18df9322ee
 ---> Running in b2924fdea197
total 12
drwxr-xr-x    2 root     root          4096 Nov  1 14:11 .
drwxr-xr-x    1 root     root          4096 Nov  1 14:11 ..
-rwxr-xr-x    1 root     root           158 Nov  1 04:45 application.properties.example
Removing intermediate container b2924fdea197
 ---> Running in 4d183760a3ae
Removing intermediate container 4d183760a3ae
 ---> Running in 705f72d07a5d
Removing intermediate container 705f72d07a5d
 ---> Running in 0605b5979c19
Removing intermediate container 0605b5979c19
 ---> Running in f238ced4e66e
Defining the UNMAZEDBOOT_RUNNER_CMD_EXEC=java $JAVA_OPTS -jar /runtime/server.jar $JAR_OPTS. Override if needed.
Removing intermediate container f238ced4e66e
 ---> Running in 87d3eba14b7f
Entrypoint will execute UNMAZEDBOOT_RUNNER_CMD_EXEC from /runtime/start.bash
Removing intermediate container 87d3eba14b7f
 ---> Running in f7be0dabbd6a
# #####################################################################
Removing intermediate container f7be0dabbd6a
 ---> Running in 7e885ce4b15b
Removing intermediate container 7e885ce4b15b
 ---> Running in c8b8e05eab0a
Removing intermediate container c8b8e05eab0a
 ---> Running in 3f5c9335461d
Removing intermediate container 3f5c9335461d
 ---> Running in c7bf4369a766
Removing intermediate container c7bf4369a766
 ---> Running in 5f2f0080b3bd
Finished preparing image with env vars BUILD_COMMIT=000000 from BUILD_BRANCH=unknown
Removing intermediate container 5f2f0080b3bd
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
Successfully tagged supercash/distance-matrix-service
```

## Running

* Just use docker-compose up, with optional `--build` to start a container.

> **ATTENTION**: Make sure to adjust the port number that the host will liste. Current is `8082`.

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
| POST  | /distancematrix | `content-type: application/json` | { "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" } |

* Response

| Headers | Payload |
| -- | -- |
| `Content-Type: application/json` | {"distance":257055,"time":13516} |

## Example

```console
$ curl -i localhost:8082/distancematrix -H 'content-type: application/json' \
       -d '{ "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" }'
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sun, 01 Nov 2020 14:33:32 GMT

{"distance":257055,"time":13516}%
```

# Automated Tests

* Gradle tests using:
  * Junit 5 for unit tests
  * Jacoco for coverage: shows percentage of coverage and generates reports

> Note: make sure to use the same version as in `tests-docker-compose.yaml`.

```console
$ gradle tests 
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

# Troubleshooting

* Rotate Google Maps token when getting errors about the token
* https://developers.google.com/maps/gmp-get-started 

```console
$ curl -i localhost:8082/distancematrix -H 'content-type: application/json' -d '{ "originAddress": "Maceio, Alagoas, Brazil", "destinationAddress": "Recife, Pernambuco, Brazil" }'
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sun, 01 Nov 2020 14:23:56 GMT

{"description":"You must enable Billing on the Google Cloud Project at https://console.cloud.google.com/project/_/billing/enable Learn more at https://developers.google.com/maps/gmp-get-started","error":500}%
```
