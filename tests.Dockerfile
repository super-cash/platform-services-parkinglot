FROM gradle:6.7.0-jdk8-hotspot

WORKDIR /build

# Just copy the needed ones
COPY build.gradle /build
COPY settings.gradle /build
COPY src/ /build/src

# Run gradle in the container
CMD ["gradle", "test"]
