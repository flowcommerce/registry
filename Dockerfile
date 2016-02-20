FROM flowdocker/play:0.0.13

ADD . /opt/play

WORKDIR /opt/play

RUN sbt clean stage
  
ENTRYPOINT ["java", "-jar", "/root/environment-provider.jar", "run", "play", "registry", "api/target/universal/stage/bin/registry-api"]
