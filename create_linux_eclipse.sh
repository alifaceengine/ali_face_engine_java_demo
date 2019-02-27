#!/bin/bash

cp -rf libs/Linux/libAliFaceEngineJNI.so .
if [ $? -ne 0 ]; then
    echo "cp libs/Linux/libAliFaceEngineJNI.so . : Fail"
    exit 1
else
    echo "cp libs/Linux/libAliFaceEngineJNI.so . : OK"
fi

./gradlew eclipse
if [ $? -ne 0 ]; then
    echo "./gradlew eclipse : Fail"
    exit 1
else
    echo "./gradlew eclipse : OK"
fi

