#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR"/html.sh" $DIR
source $DIR"/js.sh" $DIR
echo "compiling CSS"
cp "$DIR/../src/css/styles.css" "$DIR/../resources/public/css/"
