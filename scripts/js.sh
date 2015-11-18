#!/bin/bash

# add merge
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "compiling JS."
cp $DIR"/../src/js/main.js" $DIR"/../resources/public/js/main.js"
