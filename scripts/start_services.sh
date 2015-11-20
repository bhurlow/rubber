#! /bin/bash
eval $(docker-machine env default)
docker rm -f search
docker rm -f rethink
docker $(docker-machine config default) run -d --name search -p 9200:9200 elasticsearch
docker $(docker-machine config default) run -d --name rethink -p 8080:8080 -p 28015:28015 rethinkdb

