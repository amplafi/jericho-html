@if defined JERICHO_JDK_HOME echo JERICHO_JDK_HOME is set to %JERICHO_JDK_HOME%
set version=3.1
@set package_name=jericho-html-%version%
@set dependencies=compile-time-dependencies\slf4j-api-1.5.6.jar;compile-time-dependencies\commons-logging-api-1.1.1.jar;compile-time-dependencies\log4j-api-1.2.15.jar

@if defined JERICHO_JDK_HOME (
	set JERICHO_JAVAC_EXE=%JERICHO_JDK_HOME%\bin\javac
	set JERICHO_JAVADOC_EXE=%JERICHO_JDK_HOME%\bin\javadoc
	set JERICHO_JAR_EXE=%JERICHO_JDK_HOME%\bin\jar
) else (
	set JERICHO_JAVAC_EXE=javac
	set JERICHO_JAVADOC_EXE=javadoc
	set JERICHO_JAR_EXE=jar
)

@if exist test\classes rd /s/q test\classes
@if exist classes rd /s/q classes
@md classes
@if exist dist rd /s/q dist
@md dist

rem ----- Compile source:
"%JERICHO_JAVAC_EXE%" -Xlint:unchecked -g:none -classpath %dependencies% -d classes src\java\net\htmlparser\jericho\*.java src\java\net\htmlparser\jericho\nodoc\*.java
@rem *** "%JERICHO_JAVAC_EXE%" -Xlint:unchecked -g -classpath %dependencies% -d classes src\java\net\htmlparser\jericho\*.java src\java\net\htmlparser\jericho\nodoc\*.java
@if errorlevel 1 goto end

rem ----- Create jar library:
"%JERICHO_JAR_EXE%" -cf dist\%package_name%.jar -C classes .

@if "%1"=="-nojavadoc" goto buildsamples
rem ----- Create docs:
@if exist docs\javadoc rd /s/q docs\javadoc
"%JERICHO_JAVADOC_EXE%" -quiet -windowtitle "Jericho HTML Parser %version%" -classpath src\java;classes;%dependencies% -use -d docs\javadoc -subpackages net.htmlparser.jericho -exclude net.htmlparser.jericho.nodoc -noqualifier net.htmlparser.jericho -group "Core Package" net.htmlparser.jericho
type docs\src\append\stylesheet.css >> docs\javadoc\stylesheet.css
copy docs\src\replace\*.* docs\javadoc

rem ----- Build the command line samples:
:buildsamples
@if exist samples\console\classes rd /s/q samples\console\classes
@md samples\console\classes
"%JERICHO_JAVAC_EXE%" -Xlint:unchecked -g -deprecation -classpath dist/%package_name%.jar -d samples\console\classes samples\console\src\*.java
@if errorlevel 1 goto end
@if not exist samples\console\bat_lib md samples\console\bat_lib
@echo @set package_name=%package_name%> samples\console\bat_lib\set_package_name.bat

@if "%1"=="-nojavadoc" goto end
rem ----- Create the sample web application WAR file:
@if exist samples\webapps\JerichoHTML\WEB-INF\lib rd /s/q samples\webapps\JerichoHTML\WEB-INF\lib
@md samples\webapps\JerichoHTML\WEB-INF\lib
copy /y dist\%package_name%.jar samples\webapps\JerichoHTML\WEB-INF\lib
"%JERICHO_JAR_EXE%" -cf samples\webapps\JerichoHTML.war -C samples\webapps\JerichoHTML .

rem ----- Zip up the whole package:
"%JERICHO_JAR_EXE%" -cMf ..\%package_name%.zip -C .. %package_name%

:end
