FROM gradle:jdk17 AS builder
WORKDIR /nosdaq
COPY . .
RUN gradle jar



FROM ubuntu:22.04
WORKDIR /nosdaq


RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get install -y unzip gnupg curl
    # apt install cmake && 


# RUN cd baselines/eusolver
# RUN pip install -r requirements.txt
# RUN ./script/build.sh
# RUN cd /nosdaq

#RUN curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | \
#       gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg \
#       --dearmor
#
#RUN echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-7.0.list
#
#RUN apt-get update
#
#RUN apt-get install -y mongodb-org=7.0.12 mongodb-org-database=7.0.12 mongodb-org-server=7.0.12 mongodb-mongosh=2.2.12 mongodb-org-mongos=7.0.12 mongodb-org-tools=7.0.12


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


