@echo off
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Optional ENV vars
@REM   JAVA_HOME - location of a JDK home dir, required when type is jdk
@REM   MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM   MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM   MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM         set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@IF "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%

@REM Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
@REM BEGIN ANSIBLE MANAGED BLOCK
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@REM Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@REM Add default JVM options here. You can also use JAVA_OPTS and MAVEN_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8"

@REM Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto error

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto error

:execute
@REM Setup the command line

set CLASSPATH=%APP_HOME%\.mvn\wrapper\maven-wrapper.jar

@REM Download the maven-wrapper.jar if missing
if exist "%APP_HOME%\.mvn\wrapper\maven-wrapper.jar" goto wrapperDownloadEnd

echo Downloading maven-wrapper.jar...

if not "%MVNW_REPOURL%" == "" (
  set DOWNLOAD_URL=%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
) else (
  set DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
)

powershell -Command "&{"^
    "$webclient = new-object System.Net.WebClient;"^
    "if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
    "$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
    "}"^
    "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%DOWNLOAD_URL%', '%APP_HOME%\.mvn\wrapper\maven-wrapper.jar')"^
    "}"
if "%ERRORLEVEL%" == "0" goto wrapperDownloadEnd
echo Could not download maven-wrapper.jar, retrying with curl...

call curl -fsSL -o "%APP_HOME%\.mvn\wrapper\maven-wrapper.jar" "%DOWNLOAD_URL%"
if "%ERRORLEVEL%" == "0" goto wrapperDownloadEnd

echo. 1>&2
echo ERROR: Failed to download maven-wrapper.jar 1>&2
echo Please download it manually from: 1>&2
echo   %DOWNLOAD_URL% 1>&2
goto error

:wrapperDownloadEnd

@REM Now download Maven distribution if required
set WRAPPER_PROPERTIES=%APP_HOME%\.mvn\wrapper\maven-wrapper.properties

set DISTRIBUTION_URL=
for /f "usebackq eol=# tokens=1,2 delims==" %%a in ("%WRAPPER_PROPERTIES%") do (
    if "%%a" == "distributionUrl" set DISTRIBUTION_URL=%%b
)
if "%DISTRIBUTION_URL%" == "" set DISTRIBUTION_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip

set MAVEN_ZIP_NAME=apache-maven-3.9.6-bin.zip
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\%MAVEN_ZIP_NAME%

if exist "%MAVEN_HOME%\apache-maven-3.9.6\" (
  set M2_HOME=%MAVEN_HOME%\apache-maven-3.9.6
  goto wrapperExecute
)

echo Downloading Maven 3.9.6...
if not exist "%MAVEN_HOME%" mkdir "%MAVEN_HOME%"

powershell -Command "&{"^
    "$webclient = new-object System.Net.WebClient;"^
    "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;"^
    "$webclient.DownloadFile('%DISTRIBUTION_URL%', '%MAVEN_HOME%\%MAVEN_ZIP_NAME%')"^
    "}"

if "%ERRORLEVEL%" neq "0" (
  echo Failed to download Maven. Trying curl...
  curl -fsSL -o "%MAVEN_HOME%\%MAVEN_ZIP_NAME%" "%DISTRIBUTION_URL%"
)

echo Extracting Maven...
powershell -Command "Expand-Archive -Path '%MAVEN_HOME%\%MAVEN_ZIP_NAME%' -DestinationPath '%MAVEN_HOME%' -Force"
del "%MAVEN_HOME%\%MAVEN_ZIP_NAME%"

:wrapperExecute
set M2_HOME=%MAVEN_HOME%\apache-maven-3.9.6
set MAVEN_CMD_LINE_ARGS=%*

"%M2_HOME%\bin\mvn.cmd" %MAVEN_CMD_LINE_ARGS%

if %ERRORLEVEL% equ 0 goto mainEnd

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_TERMINATE_CMD%" == "" exit %ERROR_CODE%

cmd /C exit /B %ERROR_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
