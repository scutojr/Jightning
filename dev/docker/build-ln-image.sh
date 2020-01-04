#!/usr/bin/env bash

CWD=`cd $(dirname $0);pwd`
IMAGE=lightning

cd $CWD
source ../environment.sh

docker build -f lightning.Dockerfile -t $IMAGE:$LN_VERSION --build-arg LN_VERSION=$LN_VERSION --build-arg BTC_VERSION=$BTC_VERSION .
