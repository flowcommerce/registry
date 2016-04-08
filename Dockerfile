FROM flowdocker/play:0.0.44

ADD . /opt/play

WORKDIR /opt/play

RUN sbt clean stage

WORKDIR api/target/universal/stage

ENTRYPOINT ["java", "-jar", "/root/environment-provider.jar", "--service", "play", "registry", "bin/registry-api"]
