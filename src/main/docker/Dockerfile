ARG JAVA_HOME_LOCATION="/usr/lib/jvm/java-11-openjdk-amd64"

# We build Kakadu in a different build stage to keep the final container clean
FROM ubuntu:20.04 AS KAKADU_TOOL_CHAIN

ARG JAVA_HOME_LOCATION
ENV JAVA_HOME=${JAVA_HOME_LOCATION}

# Compile Kakadu if we've supplied a known kakadu.version
COPY kakadu /build/kakadu/
RUN if [ ! -z "${kakadu.version}" ]; then \
      cd /build/kakadu/"${kakadu.version}"/make && \
      apt-get update -qq && \
      DEBIAN_FRONTEND=noninteractive apt-get install -qq --no-install-recommends \
        build-essential${build.essential.version} \
        openjdk-11-jdk-headless${openjdk.version} \
        make${make.version} \
        gcc${gcc.version} \
        libtiff-tools${libtiff.version} \
        libtiff5${libtiff.version} \
        libtiff5-dev${libtiff.version} && \
      make -f Makefile-Linux-x86-64-gcc && \
      mkdir /build/kakadu/lib && \
      mkdir /build/kakadu/bin && \
      cp ../lib/Linux-x86-64-gcc/*.so /build/kakadu/lib && \
      cp ../bin/Linux-x86-64-gcc/kdu_compress /build/kakadu/bin && \
      cp ../bin/Linux-x86-64-gcc/kdu_expand /build/kakadu/bin && \
      cp ../bin/Linux-x86-64-gcc/kdu_jp2info /build/kakadu/bin ; \
    fi

# Breaking this out gives us a shorter and more obvious error message
RUN if [ -z "${kakadu.version}" ]; then \
      echo "ERROR: Missing kakadu.version build variable" && \
      exit 1 ; \
    fi

# The container image that we're going to output
FROM ubuntu:20.04

# Update packages and install tools
#  Removing /var/lib/apt/lists/* prevents using `apt` unless you do `apt update` first
RUN apt-get update -qq && \
    DEBIAN_FRONTEND=noninteractive \
    apt-get install -qq --no-install-recommends \
    libtiff-tools${libtiff.version}  \
    libtiff5${libtiff.version}  \
    libtiff5-dev${libtiff.version}  \
    openjdk-11-jre-headless${openjdk.version} \
    < /dev/null > /dev/null && \
    rm -rf /var/lib/apt/lists/*

# Put our Kakadu libs in a directory that's in the LD_LIBRARY_PATH
WORKDIR /usr/lib/jni
COPY --from=KAKADU_TOOL_CHAIN /build/kakadu/lib/* /usr/lib/jni/

# Put our Kakadu bins in the local bin directory
WORKDIR /usr/local/bin
COPY --from=KAKADU_TOOL_CHAIN /build/kakadu/bin/* /usr/local/bin/

# Set our JAVA_HOME and LD_LIBRARY_PATH
ENV LD_LIBRARY_PATH="/usr/lib/jni:${LD_LIBRARY_PATH}"
ARG JAVA_HOME_LOCATION
ENV JAVA_HOME=${JAVA_HOME_LOCATION}

# Run Kakadu non-privileged
RUN adduser --system kakadu
COPY docker-entrypoint.sh /usr/local/bin/
RUN chown -R kakadu /usr/local/bin/docker-entrypoint.sh

# Wrap things up with a generic command that will keep the container running
USER kakadu
WORKDIR /home/kakadu
ENTRYPOINT ["docker-entrypoint.sh"]
