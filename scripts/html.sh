#!/bin/sh
# 2 cycles faster

DIR=$1
# jadeing
jade $(find "$DIR/../src/html" -iname "*.jade") -P --out "resources/public"
#updating clojure
files=($(find "$DIR/../src/html" -iname "*.jade"))
for f in ${files[@]}; do
    bname=$(basename $f)
    # touch bname without extension
    touch -c "src/ombs/view/${bname%.*}.clj"
done
