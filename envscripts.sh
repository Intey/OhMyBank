#!/bin/sh

echo "add alias resetDB"
alias resetDB='bash ./resetdb.sh'
echo "add alias query"
alias query='sqlite3 database.db -cmd'
