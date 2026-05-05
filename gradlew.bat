@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem Check for JAVA_HOME
if not "%JAVA_HOME%" == "" goto okJavaHome
echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
goto fail
:okJavaHome

set DIR=%~dp0
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
set GRADLE_WRAPPER_JAR=%DIR%\gradle\wrapper\gradle-wrapper.jar
set GRADLE_WRAPPER_PROPERTIES=%DIR%\gradle\wrapper\gradle-wrapper.properties

if not exist "%GRADLE_WRAPPER_JAR%" goto downloadWrapper
goto run

:downloadWrapper
echo Downloading Gradle wrapper...
powershell -Command "& {Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar' -OutFile '%DIR%\gradle\wrapper\gradle-wrapper.jar'}"
if not exist "%GRADLE_WRAPPER_JAR%" (
    echo Failed to download gradle-wrapper.jar
    goto fail
)

:run
"%JAVA_EXE%" -jar "%GRADLE_WRAPPER_JAR%" %*
goto end

:fail
exit /b 1

:end
exit /b %ERRORLEVEL%
