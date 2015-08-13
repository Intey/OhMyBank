#!/bin/sh
files=($(ls doc/dot))
for file in ${files[*]}; do
    echo "process $file"
    dot -Tpng "doc/dot"/$file -o "doc"/$file".png"
done
