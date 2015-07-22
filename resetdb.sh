#!/bin/sh
sqlite3 database.db < migrations/onebig.down.sql
sqlite3 database.db < migrations/onebig.up.sql
sqlite3 database.db < migrations/inserts.up.sql
