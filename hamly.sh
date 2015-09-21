#!/bin/sh

hamle(){
    haml "resources/src/$1.haml" "resources/public/$1.html"
    if [[ $? != 0 ]]; then
        exit 1;
    fi
}

hamle "admin"
