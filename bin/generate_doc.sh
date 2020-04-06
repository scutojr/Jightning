#!/usr/bin/env bash


CWD=`cd $(dirname $0)/..;pwd`

mkdir -p $CWD/doc
pushd $CWD/doc
javadoc.exe -public -sourcepath $CWD/jightning-api/src/main/java -subpackages clightning
popd
