FROM ubuntu:15.10
MAINTAINER tyoras@gmail.com
LABEL Description="ShoppingAPI container" Version="0.1.0"

RUN apt-get update 
RUN apt-get install -y \
wget \
curl \
openjdk-8-jre-headless \
gettext-base \
unzip

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ENV API_SHOPPING_CONFIG_FILE_PATH=/configAPI.yml
ADD /configAPI.yml $API_SHOPPING_CONFIG_FILE_PATH
ADD /jars/shopping.jar /shopping.jar
ADD /certs/shopping.jks /certs/shopping.jks

RUN update-ca-certificates -f

# New Relic integration
ARG NEWRELIC_KEY=default_key
ENV NEWRELIC_APP_NAME Shopping_API 

#RUN ln -sf ${CATALINA_HOME}/webapps /webapps && \
#	wget -q "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" -O /tmp/newrelic.zip && \
#	unzip /tmp/newrelic.zip -d ${CATALINA_HOME}/ && \
#	rm /tmp/newrelic.zip && \
#	cd /usr/local/tomcat/newrelic && \
#	java -jar newrelic.jar install

#RUN cp ${CATALINA_HOME}/newrelic/newrelic.yml ${CATALINA_HOME}/newrelic/newrelic.yml.original && \
#	cat ${CATALINA_HOME}/newrelic/newrelic.yml.original | sed -e "s/'<\%= license_key \%>'/\'${NEWRELIC_KEY}\'/g" | sed -e "s/app_name:\ My\ Application/app_name:\ ${NEWRELIC_APP_NAME}/g" > ${CATALINA_HOME}/newrelic/newrelic.yml

EXPOSE 8443 8443
CMD java -jar /shopping.jar server /configAPI.yml














