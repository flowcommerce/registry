FROM 479720515435.dkr.ecr.us-east-1.amazonaws.com/flowcommerce/play_builder_java17_noble:latest as builder
ADD . /opt/play
WORKDIR /opt/play
RUN SBT_OPTS="-Xms1024M -Xmx2048M -Xss2M -XX:MaxMetaspaceSize=2048M" sbt clean stage

FROM 479720515435.dkr.ecr.us-east-1.amazonaws.com/flowcommerce/play_java17:latest
COPY --from=builder /opt/play/api/target/universal/stage /opt/play
WORKDIR /opt/play

ENV FLOW_ENV=production
ENV PLAY_CONFIG_RESOURCE=application.production.conf
ENTRYPOINT exec bin/registry-api -Dconfig.resource=$PLAY_CONFIG_RESOURCE

HEALTHCHECK --interval=5s --timeout=5s --retries=10 \
  CMD curl -f http://localhost:9000/_internal_/ready || exit 1
