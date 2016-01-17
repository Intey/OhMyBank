#!/bin/sh

files=($(find doc/dot -type f))
for file in ${files[*]}; do
    echo "process $file"
    bn=$(basename $file)
    dot -Tpng $file -o "doc/pngs"/$bn".png"
done
