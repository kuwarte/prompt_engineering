#!/bin/bash
#
# chmod +x run.sh <-- prov_permission
# my custom .java compile and run with loader
#
#
spinner() {
    local pid=$1
    local delay=0.1
    local spinstr='|/-\'
    local msg="$2"

    while kill -0 $pid 2>/dev/null; do
        for ((i=0; i<${#spinstr}; i++)); do
            printf "\r%s %c" "$msg" "${spinstr:$i:1}"
            sleep $delay
        done
    done

    wait $pid
    local status=$?
    if [ $status -eq 0 ]; then
        printf "\r%s Done!  \n" "$msg"
    else
        printf "\r%s Failed!  \n" "$msg"
        exit $status
    fi
}
#
clear
#
[ -d bin ] && rm -f bin/*.class
#
javac -d bin src/main/java/*.java &
pid=$!
spinner $pid "Compiling Java Files..."
#
echo "Running Java File..."
echo
java -cp bin Main



