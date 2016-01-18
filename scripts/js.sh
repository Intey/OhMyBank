#!/bin/bash

DIR=$1
SRC="$DIR/../src/js"
DEST="$DIR/../resources/public/js"
echo "compiling JS."

files=($(find $SRC -iname "*.js"))
for f in ${files[@]}; do
    fname=$(basename $f)
    echo "cp $f ⇒ resources/public/js/$fname"
    cp $f $DEST"/$fname"
done

browserify -t [ babelify --presets [ react ] ] $SRC"/jsx/components.jsx" -o $DEST"/components.js" > /dev/stdout
