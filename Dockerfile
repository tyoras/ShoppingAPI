FROM ubuntu:17.10
MAINTAINER Tyoras <tyoras@gmail.com>
LABEL Description="ShoppingAPI container" Version="0.3.1"

RUN apt-get update && apt-get install -y \
  wget \
  openjdk-8-jre-headless \
  gettext-base \
  unzip \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV API_SHOPPING_CONFIG_FILE_PATH=/opt/shopping_api/configAPI.yml

RUN mkdir -p /opt/shopping_api/certs
WORKDIR /opt/shopping_api
COPY docker/api/configAPI.yml $API_SHOPPING_CONFIG_FILE_PATH
COPY docker/api/entrypoint.sh /opt/shopping_api/entrypoint.sh
RUN chmod +x entrypoint.sh
COPY target/shopping.jar shopping.jar
COPY docker/api/certs/shopping.jks certs/shopping.jks

RUN update-ca-certificates -f

# New Relic integration
ARG NEWRELIC_KEY=default_key
ENV NEWRELIC_APP_NAME Shopping_API

RUN wget -q "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" -O /tmp/newrelic.zip && \
  unzip /tmp/newrelic.zip -d /opt/shopping_api/ && \
  rm /tmp/newrelic.zip

#RUN cp newrelic/newrelic.yml newrelic/newrelic.yml.original && \
#  cat newrelic/newrelic.yml.original | sed -e "s/'<\%= license_key \%>'/\'${NEWRELIC_KEY}\'/g" | sed -e "s/app_name:\ My\ Application/app_name:\ ${NEWRELIC_APP_NAME}/g" > newrelic/newrelic.yml

EXPOSE 8443 8443
USER root
ENTRYPOINT ["/opt/shopping_api/entrypoint.sh"]














