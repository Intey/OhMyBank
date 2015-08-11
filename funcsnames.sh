#!/bin/sh

function borderedMessage {
    echo "================================================================================"
    echo "$1" 
    echo "================================================================================"
}

function join {
  # out=""
  for e in $1; do
    # out="$out $e"
    echo $e
  done
}

function joinArray { # not work in my case
  local IFS="$1"
  shift
  echo "$*"
}

funcs=( $(grep "defn" -R $1 | cut -d' ' -f 2) ) # additional paranthes for array
for f in $funcs; do
  # get usages
  f_usage=( $(grep "$f" -R src/ombs/ | grep -vn "defn" ) ) 
  echo
  echo "func $f"
  for u in $f_usage; do
    echo "==== f_usage: $f_usage"
  done
  # borderedMessage "usage of $f $(join $f_usage)"
done

