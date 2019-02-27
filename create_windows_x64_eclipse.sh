#!/bin/bash

cp -rf libs/Windows/x64/AliFaceEngineJNI.dll .
if [ $? -ne 0 ]; then
    echo "cp -rf libs/Windows/x64/AliFaceEngineJNI.dll . : Fail"
    exit 1
else
    echo "cp -rf libs/Windows/x64/AliFaceEngineJNI.dll . : OK"
fi

cp -rf libs/Windows/x64/pthreadVC2.dll .
if [ $? -ne 0 ]; then
    echo "cp -rf libs/Windows/x64/pthreadVC2.dll . : Fail"
    exit 1
else
    echo "cp -rf libs/Windows/x64/pthreadVC2.dll . : OK"
fi

./gradlew eclipse
if [ $? -ne 0 ]; then
    echo "./gradlew eclipse : Fail"
    exit 1
else
    echo "./gradlew eclipse : OK"
fi

