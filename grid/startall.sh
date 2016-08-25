#!/bin/bash

#. gf.config

# Issue commands to gfsh to start locator and launch a server
echo "Starting locator and server..."
gfsh <<!
start locator --name=locator1 --port=10334 --properties-file=config/locator.properties --load-cluster-configuration-from-dir=true --initial-heap=256m --max-heap=256m

start server --name=server1 --server-port=0 --properties-file=config/gemfire.properties --initial-heap=1g --max-heap=1g
#start server --name=server2 --server-port=0 --properties-file=config/gemfire.properties --initial-heap=1g --max-heap=1g
#start server --name=server3 --server-port=0 --properties-file=config/gemfire.properties --initial-heap=1g --max-heap=1g

deploy --jar=../target/clusterManager-0.0.1.jar

list members;
list regions;
exit;
!
