#!/bin/bash

cp -rf libs/Windows/x64/AliFaceEngineJNI.dll .
cp -rf libs/Windows/x64/pthreadVC2.dll .
./gradlew eclipse
