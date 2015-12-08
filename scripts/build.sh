#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR"/html.sh"
source $DIR"/js.sh"
cp "$DIR/../src/css/styles.css" "$DIR/../resources/public/css/"
