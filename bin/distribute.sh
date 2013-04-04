#!/bin/sh

VERSION_FILE=./utils/src/main/resources/version.properties
SSH_TARGET=ssh.thathost.com:/usr/local/share/htdocs.basus/download

if test \! -f ./bin/update-version.pl
then
    echo "Must be run from the project root"
    exit 1
fi

./bin/update-version.pl
svn commit -m "Updated version" $VERSION_FILE
mvn clean package
JAR_FILES="./applet/target/basus-runner-[0-9]*.jar ./full/target/basus-[0-9]*.jar"
for q in $JAR_FILES
do
    if test \! -f $q
    then
        echo "No file $q"
        exit 1
    fi
done

scp $JAR_FILES $VERSION_FILE TODO.txt CHANGES.txt $SSH_TARGET

echo "Remember to update symlinks and web pages on the server."
