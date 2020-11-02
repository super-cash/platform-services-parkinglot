### Builder Arguments
ARG UNMAZEDBOOT_BUILDER_GIT_SHA=${UNMAZEDBOOT_BUILDER_GIT_SHA:-000000}
ARG UNMAZEDBOOT_BUILDER_GIT_BRANCH=${UNMAZEDBOOT_BUILDER_GIT_BRANCH:-master}
ARG UNMAZEDBOOT_BUILDER_GRADLE_BUILD_CMD="gradle build -x test"
ARG UNMAZEDBOOT_BUILDER_DIR="build/libs"
ARG UNMAZEDBOOT_BUILDER_PACKAGE_EXTENSION="jar"
ARG UNMAZEDBOOT_BUILDER_GRADLE_VERSION=${UNMAZEDBOOT_BUILDER_GRADLE_VERSION:-latest}

### Linker Argumentss
ARG UNMAZEDBOOT_LINKER_VERSION=${UNMAZEDBOOT_LINKER_VERSION:-latest}

### Runner Arguments
ARG UNMAZEDBOOT_RUNNER_PORT="8080"
ARG UNMAZEDBOOT_RUNNER_VERSION=${UNMAZEDBOOT_RUNNER_VERSION:-latest}

# #####################################################################
# Build stage for building the target directory before running tests
# #####################################################################
FROM marcellodesales/unmazedboot-builder-gradle:${UNMAZEDBOOT_BUILDER_GRADLE_VERSION} as unmazedboot-builder-artifacts

# #####################################################################
# Build stage for making a jlink specific for the app
# #####################################################################
FROM intuit/unmazedboot-linker:${UNMAZEDBOOT_LINKER_VERSION} as unmazedboot-jdk-linker

# #####################################################################
# Build stage for running the runtime image (MUST MATCH LINKER TYPE)
# #####################################################################
FROM intuit/unmazedboot-runner:${UNMAZEDBOOT_RUNNER_VERSION}

# Alpine needs the SSL certificates from the JVM
# javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed
# https://stackoverflow.com/questions/53246399/jdk8-jdk10-pkix-path-building-failed-suncertpathbuilderexception-unable-to/53246850#53246850
# Inspected the builder docker run -ti intuit/unmazedboot-builder-gradle:5.0.0-jdk8-alpine-0.5.0 ls -la /usr/lib/jvm/ and found the correct path
# ALPINE: COPY --from=unmazedboot-builder-artifacts /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts /etc/ssl/certs/java/cacerts
COPY --from=unmazedboot-builder-artifacts /opt/java/openjdk/jre/lib/security/cacerts /etc/ssl/certs/java/cacerts

# The location of the custom jvm is /opt/jdk-custom/jre 
# https://github.com/intuit/unmazedboot/blob/master/runner/custom-jlink-jdk/Dockerfile#L26
RUN rm -f /opt/jdk-custom/jre/lib/security/cacerts
RUN ln -s /etc/ssl/certs/java/cacerts /opt/jdk-custom/jre/lib/security/cacerts
