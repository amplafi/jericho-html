@if defined JERICHO_JDK_HOME echo JERICHO_JDK_HOME is set to %JERICHO_JDK_HOME%
@set dependencies_test=classes;samples\console\classes;test\lib\junit-4.5.jar

@if defined JERICHO_JDK_HOME (
	set JERICHO_JAVAC_EXE=%JERICHO_JDK_HOME%\bin\javac
	set JERICHO_JAVA_EXE=%JERICHO_JDK_HOME%\bin\java
) else (
	set JERICHO_JAVAC_EXE=javac
	set JERICHO_JAVA_EXE=java
)

rem ----- Compile tests:
@if exist test\classes rd /s/q test\classes
@md test\classes
"%JERICHO_JAVAC_EXE%" -Xlint:unchecked -g -classpath %dependencies_test% -d test\classes test\src\*.java test\src\samples\*.java test\src\net\htmlparser\jericho\*.java
@if errorlevel 1 goto end

rem ----- Run tests:
"%JERICHO_JAVA_EXE%" -classpath %dependencies_test%;test\classes -Djava.util.logging.config.file=test\logging.properties org.junit.runner.JUnitCore TestSuite
@if errorlevel 1 goto end

:end
