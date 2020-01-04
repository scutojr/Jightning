#!/usr/bin/env bash

CWD=`cd $(dirname $0);pwd`
IMAGE=bitcoin

cd $CWD
source ../environment.sh

docker build -f bitcoin.Dockerfile -t $IMAGE:$BTC_VERSION --build-arg BTC_VERSION=$BTC_VERSION .
