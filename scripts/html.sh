#!/bin/sh
# 2 cycles faster

jade $(find src/html -iname "*.jade") -P --out "resources/public"

files=($(find src/html -iname "*.jade"))
for f in ${files[@]}; do
    bname=$(basename $f)
    touch -c "src/ombs/view/${bname%.*}.clj"
done
