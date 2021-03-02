FROM gradle:6.7.0-jdk8-hotspot

# Just the builder
WORKDIR /builder

# Just copy the needed ones
COPY build.gradle /builder
COPY settings.gradle /builder
COPY src/ /builder/src

# Run gradle in the container
CMD ["gradle", "test"]
