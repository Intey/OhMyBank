#!/bin/sh 
if [[ -z $1 ]]; then
    echo "one parameter - db name"
    exit 1
fi
dbname=$1
sqlite3 "$dbname".db < migrations/onebig.down.sql
sqlite3 "$dbname".db < migrations/onebig.up.sql
sqlite3 "$dbname".db < migrations/inserts.up.sql
