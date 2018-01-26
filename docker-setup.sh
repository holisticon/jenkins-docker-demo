#!/usr/bin/env bash

export COMPOSE_PROJECT_NAME=holisticon

# Remove existing containers
docker-compose down --volumes
docker stop 
docker rm --volumes -f 

# create local node image
docker-compose build jenkins_node_1
docker-compose up -d registry
sleep 20 && docker-compose push jenkins_node_1

## Start new containers
docker-compose up --build
