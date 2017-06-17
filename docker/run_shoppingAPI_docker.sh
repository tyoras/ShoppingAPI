#!/bin/sh
#docker run -d -p 8080:8080 --name shoppingAPI --link mongo_docker tyoras/shopping_api:$1
source env.sh

rm -rf docker-compose.yml

envsubst < "docker-compose-template.yml" > "docker-compose.yml"
docker-compose up -d
