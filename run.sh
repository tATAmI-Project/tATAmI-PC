#!/bin/bash

echo Use this only if you do not wish to set arguments in BootDefaultArguments and run Boot
echo Usage: run.sh simulation-file-xml main-host main-port local-host local-port screen-area-width screen-area-height local-container-name
echo Defaults are: default null -1 null -1 800 600 null

if [ "$1" == "--help" ]; then
	exit 0
fi
if [ "$1" == "-h" ]; then
	exit 0
fi

echo ===============================================================

java -Djava.ext.dirs=libs:libs-kestrel:libs-json -cp bin tatami.simulation.Boot "$@"

echo ===============================================================
echo Press any key
read -n 1 -s
exit 0
