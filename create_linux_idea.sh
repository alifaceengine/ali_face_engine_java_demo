#!/bin/bash

cp -rf libs/Linux/libAliFaceEngineJNI.so .
if [ $? -ne 0 ]; then
    echo "cp libs/Linux/libAliFaceEngineJNI.so . : Fail"
    exit 1
else
    echo "cp libs/Linux/libAliFaceEngineJNI.so . : OK"
fi

./gradlew idea
if [ $? -ne 0 ]; then
    echo "./gradlew idea : Fail"
    exit 1
else
    echo "./gradlew idea : OK"
fi

