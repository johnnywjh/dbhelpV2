#!/bin/bash -il
docker rmi --force `docker images | grep dbhelp | awk '{print $3}'`