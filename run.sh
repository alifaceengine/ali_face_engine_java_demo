#!/bin/bash

SYSTEM_NAME=$1
DEMO=$2

echo ====== $SYSTEM_NAME ======
if [ -z "$SYSTEM_NAME" ]
then
	echo "SYSTEM_NAME is empty"
	exit
fi

if [ "$SYSTEM_NAME" == "Windows" ];then
cp -rf ./libs/${SYSTEM_NAME}/x64/* .
echo "cp libs success!"
fi

javac -encoding utf-8 -d . ./src/main/java/com/alibaba/cloud/faceengine/*.java src/main/java/${DEMO}.java src/main/java/Utils.java 
java -Dfile.encoding=utf-8 -Djava.library.path=./libs/${SYSTEM_NAME}/ -Djava.library.path=./ ${DEMO}
