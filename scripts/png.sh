#!/bin/sh
cond=$1
if [[ -z $cond ]]; then
    echo "Give grep search string"
    exit 1
fi

echo "use $cond"

files=($(grep $cond -R doc/dot -l))
for file in ${files[*]}; do
    echo "process $file"
    bn=$(basename $file)
    dot -Tpng $file -o "doc/pngs"/$bn".png"
done
