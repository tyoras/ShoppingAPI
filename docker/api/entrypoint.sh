#!/bin/sh
set -e

# customize New Relic conf
cp /opt/shopping_api/newrelic/newrelic.yml /opt/shopping_api/newrelic/newrelic.yml.original
cat /opt/shopping_api/newrelic/newrelic.yml.original | sed -e "s/'<\%= license_key \%>'/\'${NEWRELIC_KEY}\'/g" | sed -e "s/app_name:\ My\ Application/app_name:\ ${NEWRELIC_APP_NAME}/g" > /opt/shopping_api/newrelic/newrelic.yml

exec java -javaagent:/opt/shopping_api/newrelic/newrelic.jar -jar /opt/shopping_api/shopping.jar server /opt/shopping_api/configAPI.yml
