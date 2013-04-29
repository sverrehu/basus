#!/bin/bash

function usage()
{
    cat << EOF
usage: $0 [options]

This script will spellcheck the help documentation.

OPTIONS:
   -h      Show this message
   -l      Language [nb,en]
   -s      Check spellinng
   -c      Check links
   -x      Check well-formed xml
EOF
}

function list_lang()
{
    cat << EOF
Available languages are
nb - Norsk bokmål
en - English 
EOF
}

function link_check()
{
    FILE=$1
    echo "[LINK]" $(basename $FILE)
    linkchecker $FILE -ocsv | cut -s -d';' -f1,9 | tail -n +2
}

function xml_check()
{
    FILE=$1
    echo "[XML]" $(basename $FILE)
    echo "<root>" > /tmp/check.xml
    cat $FILE >> /tmp/check.xml
    echo "</root>" >> /tmp/check.xml
    xmllint /tmp/check.xml --noout
    if [ $? -ne 0 ]
    then
	error "xml validation error in $FILE"
    fi
}

function spell_check()
{
    FILE=$1
    echo "[SPELL]" $(basename $FILE)
    hunspell -d $DICTIONARY -p ./personal.dict -i UTF-8 $FILE
}

function error()
{
    red=$(tput setaf 1)
    reset=$(tput setaf sgr0)
    echo "${red}Error${reset}" $1
}

LANG=
CHECK_SPELLING=
CHECK_LINK=
CHECK_XML=

while getopts “hl:scx” OPTION
do
    case $OPTION in
        h)
            usage
            exit 1
            ;;
        l)
            LANG=$OPTARG
            ;;
        s)
            CHECK_SPELLING=y
            ;;
        c)
            CHECK_LINK=y
            ;;
        x)
            CHECK_XML=y
            ;;
        ?)
            usage
            exit
            ;;
    esac
done

if [[ -z $LANG ]]
then
    usage
    exit 1
fi

DIR=
DICTIONARY=
case "$LANG" in
    nb)
	DIR=src/main/resources/help/nb
	DICTIONARY=nb_NO
	;;
    en)
	DIR=src/main/resources/help/en
	DICTIONARY=en_GB
	;;
    *)
	list_lang
	exit
	;;
esac


FILES=$(find $DIR -name *.html)
for FILE in $FILES
do
    if [ -n "$CHECK_LINK" ]
    then
	link_check $FILE
    fi

    if [ -n "$CHECK_XML" ]
    then
	xml_check $FILE
    fi

    if [ -n "$CHECK_SPELLING" ]
    then
	spell_check $FILE
    fi
done
