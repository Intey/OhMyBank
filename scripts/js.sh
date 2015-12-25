#!/bin/bash

DIR=$1

echo "compiling JS."

#TODO add merge
files=($(find "$DIR/../src/js" -iname "*.js"))
for f in ${files[@]}; do
    bname=$(basename $f)
    echo "cp $f ⇒ resources/public/js/$bname"
    cp $f "$DIR/../resources/public/js/$bname"
done
