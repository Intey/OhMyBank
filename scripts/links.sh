#!/bin/sh
if [[ -z $1 && -z $2 ]]; then
  echo "first arg is dir where from i get defned function, and then start search it in second arg"
  exit 1
fi

src=$1
trg=$2

files=$(find $src -iname "*.clj")

funcs=$(grep "defn" -R $files | cut -d' ' -f 2)

echo $funcs

targets=$(find $src -iname "*.clj")

output=()
for f in ${funcs[*]}; do
  output+=$(grep "$f" -R $trg -l)
done
# echo ${output[*]}
