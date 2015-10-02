#!/bin/sh

hamle(){
    haml "src/haml/$1.haml" "resources/public/$1.html"
    if [[ $? != 0 ]]; then
        exit 1;
    fi
}

for file in $(ls src/haml); do
    hamle "${file%.*}"
done
