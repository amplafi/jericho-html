@if "%1"=="" goto error
@call bat_lib\set_package_name
java -classpath classes;../../dist/%package_name%.jar -enableassertions -Djava.util.logging.config.file=logging.properties %*
@goto end
:error
@echo You must specify the name of a sample program to run on the command line
:end
@pause
