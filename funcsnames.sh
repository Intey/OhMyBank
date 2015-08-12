#!/bin/sh
IFS_back=$IFS
IFS=$'\n'

if [[ -z $1 ]]; then
    echo "first arg should be searching function" 
    exit 1
fi

borderedMessage() {
    echo "================================================================================"
    echo "$1" 
    echo "================================================================================"
}

# join() {
#     for e in $1; do
#         # out="$out $e"
#         echo $e
#     done
# }
# 
# joinArray() {
#     # not work in my case
#     local IFS="$1"
#     shift
#     echo "$*"
# }

main() {
    funcs=($(grep "defn" -R $1 | cut -d' ' -f 2)) # additional paranthes for array
    # echo "found funcs:"
    # echo ${funcs[*]}
    for f in ${funcs[@]}; do
        # get usages
        f_usage=( $(grep "$f" -R src/ombs/ | grep -v "defn" ) ) 
        count=0
        for u in ${f_usage[@]}; do
            ((count++))
        done

        # print usages
        echo "$f: $count" # (in db, core, etc.)" 
        # borderedMessage "usage of $f $(join $f_usage)"

        if [[ $2 == "--verbose" ]]; then
            for u in ${f_usage[@]}; do
                # echo $u
                # usage_line=$(echo $u | cut -d':' -f 3) 
                usage_line=$(echo $u | awk 'BEGIN {FS="[ ]{2,}"} {print $1 "|||" $2}') # for hard delimiter
                # usage_line=$(echo $u)
                if [[ ${usage_line:0:1} != ";" ]]; then
                    echo $usage_line
                fi
            done
        fi
    done
    exit 0
}
main $1 $2

IFS=$IFS_back
