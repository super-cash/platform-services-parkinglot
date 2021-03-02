FROM gradle:6.7.1-jdk15-hotspot

# Just the builder tests
WORKDIR /builder

# Just copy the needed ones
COPY build.gradle /builder

# The settings
COPY settings.gradle /builder

# The source dir
COPY src/ /builder/src

# Run gradle in the container
CMD ["gradle", "test"]
