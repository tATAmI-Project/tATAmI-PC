@echo Use this only if you do not wish to set arguments in BootDefaultArguments and run Boot
@echo Usage: simulation-file-xml main-host main-port local-host local-port screen-area-width screen-area-height local-container-name
@echo Defaults are: default null -1 null -1 800 600 null
@echo ===============================================================
@echo off

java -Djava.ext.dirs=libs -cp bin tatami.simulation.Boot %*

@echo ===============================================================
@pause
REM @exit