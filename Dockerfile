### Builder Arguments trigger
ARG CI_JOB_URL=${CI_JOB_URL:---no-CI_JOB_URL-provided--}
ARG UNMAZEDBOOT_BUILDER_GIT_SHA=${UNMAZEDBOOT_BUILDER_GIT_SHA:-000000}
ARG UNMAZEDBOOT_BUILDER_GIT_BRANCH=${UNMAZEDBOOT_BUILDER_GIT_BRANCH:-master}
# https://stackoverflow.com/questions/69960853/unable-to-list-file-systems-to-check-whether-they-can-be-watched
ARG UNMAZEDBOOT_BUILDER_GRADLE_BUILD_CMD="gradle build -x test --no-watch-fs --no-daemon"
ARG UNMAZEDBOOT_BUILDER_DIR="build/libs"
ARG UNMAZEDBOOT_BUILDER_PACKAGE_EXTENSION="jar"
ARG UNMAZEDBOOT_BUILDER_GRADLE_VERSION=${UNMAZEDBOOT_BUILDER_GRADLE_VERSION:-latest}

### Linker Argumentss
ARG UNMAZEDBOOT_LINKER_VERSION=${UNMAZEDBOOT_LINKER_VERSION:-latest}

# Bug running Custom JVM without jdk.crypto.cryptoki  https://stackoverflow.com/questions/58027309/how-to-enable-ecdhe-ciphers-with-openjdk-14-on-an-alpine-docker-container/58059795#58059795
ARG UNMAZEDBOOT_LINKER_JDK_MODULES=java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.crypto.cryptoki

### Runner ArgumentsBUILD_TAG
ARG UNMAZEDBOOT_RUNNER_PORT="8082"
ARG UNMAZEDBOOT_RUNNER_VERSION=${UNMAZEDBOOT_RUNNER_VERSION:-latest}

# #####################################################################
# Build stage for building the target directory before running tests
# #####################################################################
FROM marcellodesales/unmazedboot-builder-gradle:${UNMAZEDBOOT_BUILDER_GRADLE_VERSION} AS unmazedboot-builder-artifacts
ENV UNMAZEDBOOT_BUILDER_GIT_SHA ${UNMAZEDBOOT_BUILDER_GIT_SHA:-000000}
ENV UNMAZEDBOOT_BUILDER_GIT_BRANCH ${UNMAZEDBOOT_BUILDER_GIT_BRANCH:-develop}
ENV UNMAZEDBOOT_BUILDER_GIT_PIPELINE_URL ${UNMAZEDBOOT_BUILDER_GIT_BRANCH:-develop}

# #####################################################################
# Build stage for making a jlink specific for the app
# #####################################################################
FROM marcellodesales/unmazedboot-linker:${UNMAZEDBOOT_LINKER_VERSION} AS unmazedboot-jdk-linker


# Hack to sync the parallel build and slow-down the build
COPY --from=unmazedboot-builder-artifacts /app/build/resources/main/banner.txt /app/banner.txt

# #####################################################################
# Build stage for running the runtime image (MUST MATCH LINKER TYPE)
# #####################################################################
FROM marcellodesales/unmazedboot-runner:${UNMAZEDBOOT_RUNNER_VERSION}

# Hack the sync the parallel build and slow-down the build to runtime
COPY --from=unmazedboot-jdk-linker /app/banner.txt /banner.txt

# Alpine needs the SSL certificates from the JVM
# javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed
# https://stackoverflow.com/questions/53246399/jdk8-jdk10-pkix-path-building-failed-suncertpathbuilderexception-unable-to/53246850#53246850
# Inspected the builder docker run -ti intuit/unmazedboot-builder-gradle:5.0.0-jdk8-alpine-0.5.0 ls -la /usr/lib/jvm/ and found the correct path
# ALPINE: COPY --from=unmazedboot-builder-artifacts /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts /etc/ssl/certs/java/cacerts
COPY --from=unmazedboot-builder-artifacts /opt/java/openjdk/lib/security/cacerts /etc/ssl/certs/java/cacerts

# The location of the custom jvm is /opt/jdk-custom/jre
# https://github.com/intuit/unmazedboot/blob/master/runner/custom-jlink-jdk/Dockerfile#L26
RUN rm -f /opt/jdk-custom/jre/lib/security/cacerts && \
    ln -s /etc/ssl/certs/java/cacerts /opt/jdk-custom/jre/lib/security/cacerts
