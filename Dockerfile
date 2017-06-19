FROM flowdocker/play_builder:0.0.69 as builder
ADD . /opt/play
WORKDIR /opt/play
RUN sbt clean stage

FROM flowdocker/play:0.0.69
COPY --from=builder /opt/play /opt/play
WORKDIR api/target/universal/stage
ENTRYPOINT ["java", "-jar", "/root/environment-provider.jar", "--service", "play", "registry", "bin/registry-api"]
HEALTHCHECK --interval=5s --timeout=5s --retries=10 \
  CMD curl -f http://localhost:9000/_internal_/healthcheck || exit 1
