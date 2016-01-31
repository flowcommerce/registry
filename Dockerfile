FROM flowcommerce/play:0.0.7

ADD . /opt/play

WORKDIR /opt/play

RUN sbt clean stage

CMD "java -jar /root/environment-provider.jar run play registry api/target/universal/stage/bin/registry-api $env"
