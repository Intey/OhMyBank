if [[ -z $1 ]]; then
    echo "need filename"
    exit 1
fi

find $@ -iname "*clj" | xargs sed -i "s/\s\+$//"
