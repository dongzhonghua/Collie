#!/bin/zsh
set -ex
if [ -n "$1" ]; then
    mvn install
fi

cp collie-agent/target/collie-agent-1.0-SNAPSHOT-jar-with-dependencies.jar ${HOME}/.collie/collie-agent.jar
cp collie-core/target/collie-core-1.0-SNAPSHOT-jar-with-dependencies.jar ${HOME}/.collie/collie-core.jar
cp collie-spy/target/collie-spy-1.0-SNAPSHOT.jar ${HOME}/.collie/collie-spy.jar
cp collie-test/target/collie-test-1.0-SNAPSHOT.jar ${HOME}/.collie/collie-test.jar
ls ${HOME}/.collie
java -javaagent:${HOME}/.collie/collie-agent.jar=${HOME}/.collie/collie-core.jar,${HOME}/.collie/collie-spy.jar -jar ${HOME}/.collie/collie-test.jar