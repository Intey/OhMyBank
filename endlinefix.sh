if [[ -z $1 ]]; then
    echo "need filename"
    exit 1
fi
if [[ -f $1 ]]; then
    sed -i "s/\s\+$//" $1
    exit 0
fi
if [[ -d $1 ]]; then
    find $@ -iname "*clj" | xargs sed -i "s/\s\+$//"
    echo "process dir $1"
    exit 0
fi

