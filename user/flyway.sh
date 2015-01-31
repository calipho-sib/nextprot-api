#!/bin/bash

if [ -z "$1" ] 
    then 
        echo "Specify database example:crick.isb-sib.ch:5432/np_unit"
    exit 0
fi

if [ -z "$2" ]
    then
        echo "Specify the user example: postgrest"
        exit 0
fi

if [ -z "$3" ]
    then
        echo "Specify the password"
        exit 0
fi

if [ -z "$4" ]
    then
        echo "Specify the target example: migrate"
        exit 0
fi


DB_URL=${1}
DB_USER=${2}
DB_PWD=${3}

set -x
mvn -Dflyway.driver=org.postgresql.Driver -Dflyway.url=jdbc:postgresql://$DB_URL -Dflyway.password=$DB_PWD -Dflyway.user=$DB_USER -Dflyway.schemas=np_users flyway:$4 
set +x
