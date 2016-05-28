FROM ubuntu:15.10
MAINTAINER tyoras@gmail.com
LABEL Description="ShoppingAPI container" Version="0.1.0"

RUN apt-get update 
RUN apt-get install -y \
wget \
curl \
gnupg \
openjdk-8-jre-headless \
gettext-base \
unzip

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME

# see https://www.apache.org/dist/tomcat/tomcat-8/KEYS
RUN gpg --keyserver pool.sks-keyservers.net --recv-keys \
	05AB33110949707C93A279E3D3EFE6B686867BA6 \
	07E48665A34DCAFAE522E5E6266191C37C037D42 \
	47309207D818FFD8DCD3F83F1931D684307A10A5 \
	541FBE7D8F78B25E055DDEE13C370389288584E7 \
	61B832AC2F1C5A90F0F9B00A1C506407564C17A3 \
	79F7026C690BAA50B92CD8B66A3AD3F4F22C4FED \
	9BA44C2621385CB966EBA586F72C284D731FABEE \
	A27677289986DB50844682F8ACB77FC2E86E29AC \
	A9C5DF4D22E99998D9875A5110C01C5A2F6059E7 \
	DCFD35E0BF8CA7344752DE8B6FB21E8933C60243 \
	F3A04C595DB5B6A5F1ECA43E3B7BBB100D811BBE \
	F7DA48BB64BCB84ECBA7EE6935CD23C10D498E23

ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.0.35
ENV TOMCAT_TGZ_URL https://www.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

RUN set -x \
	&& curl -fSL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz \
	&& curl -fSL "$TOMCAT_TGZ_URL.asc" -o tomcat.tar.gz.asc \
	&& gpg --verify tomcat.tar.gz.asc \
	&& tar -xvf tomcat.tar.gz --strip-components=1 \
	&& rm bin/*.bat \
	&& rm tomcat.tar.gz* \
	&& rm -rf $CATALINA_HOME/webapps/ROOT \
	&& rm -rf $CATALINA_HOME/webapps/examples \
	&& rm -rf $CATALINA_HOME/webapps/docs

# Config Catalina
ENV CATALINA_OPTS="-Xms900m -Xmx900m -verbose:gc -XX:+PrintGCDetails -XX:+PrintTenuringDistribution -server"

ENV API_SHOPPING_CONFIG_FILE_PATH="$CATALINA_HOME/configAPI.properties"
ADD /configAPI.properties $API_SHOPPING_CONFIG_FILE_PATH
ADD /wars/shopping.war ${CATALINA_HOME}/webapps/shopping.war
ADD /certs/shopping.jks /certs/shopping.jks
ADD /server.xml ${CATALINA_HOME}/conf/server.xml

RUN update-ca-certificates -f

# New Relic integration
ARG NEWRELIC_KEY=default_key
ENV NEWRELIC_APP_NAME Shopping_API 

RUN apt-get update && apt-get install -y \ 
	unzip \
	wget

RUN ln -sf ${CATALINA_HOME}/webapps /webapps && \
	wget -q "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" -O /tmp/newrelic.zip && \
	unzip /tmp/newrelic.zip -d ${CATALINA_HOME}/ && \
	rm /tmp/newrelic.zip && \
	cd /usr/local/tomcat/newrelic && \
	java -jar newrelic.jar install

RUN cp ${CATALINA_HOME}/newrelic/newrelic.yml ${CATALINA_HOME}/newrelic/newrelic.yml.original && \
	cat ${CATALINA_HOME}/newrelic/newrelic.yml.original | sed -e "s/'<\%= license_key \%>'/\'${NEWRELIC_KEY}\'/g" | sed -e "s/app_name:\ My\ Application/app_name:\ ${NEWRELIC_APP_NAME}/g" > ${CATALINA_HOME}/newrelic/newrelic.yml

EXPOSE 8080 8080
CMD ["catalina.sh", "run"]














