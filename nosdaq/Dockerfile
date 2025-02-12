FROM gradle:jdk17 AS builder
WORKDIR /nosdaq
COPY . .
RUN gradle jar



FROM ubuntu:22.04
WORKDIR /nosdaq


RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get install -y unzip gnupg curl && \
    apt-get install -y cmake python3-pip



COPY baselines/eusolver ./baselines/eusolver
COPY src/main/resources ./src/main/resources
WORKDIR /nosdaq/baselines/eusolver
RUN pip3 install pyparsing z3-solver
RUN pip3 install -r requirements.txt
RUN chmod +x scripts/build.sh && ./scripts/build.sh

WORKDIR /nosdaq


COPY --from=builder /nosdaq/build/libs/nosdaq-1.0-SNAPSHOT.jar .
COPY --from=builder /nosdaq/lib ./lib


COPY run.sh .
RUN chmod +x run.sh

COPY main.py .

ADD "https://github.com/Z3Prover/z3/releases/download/z3-4.12.2/z3-4.12.2-x64-glibc-2.35.zip" ./
RUN unzip z3-4.12.2-x64-glibc-2.35.zip && rm z3-4.12.2-x64-glibc-2.35.zip

ENV Z3_HOME="/nosdaq/z3-4.12.2-x64-glibc-2.35"
ENV LD_LIBRARY_PATH="$Z3_HOME/bin:$LD_LIBRARY_PATH"

CMD ["tail", "-f", "/dev/null"]


