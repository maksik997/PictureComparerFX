@echo off
setlocal enabledelayedexpansion
set ERRORLEVEL=0

call :check_command mvn
call :check_command java
call :check_command jpackage

if not exist "pom.xml" (
    echo Error: Run this script from the project root directory (where pom.xml is located).
    exit /b 1
)

echo Building with Maven...
mvn clean package
if %ERRORLEVEL% neq 0 (
    echo Maven build failed. Aborting.
    exit /b 1
)

mkdir build\Windows
mkdir build\temp

for /f "delims=" %%i in ('dir /b /s target\*SHADED.jar') do (
    set JAR_FILE=%%i
)

if not defined JAR_FILE (
    echo Error: No SHADED JAR file found. Maven build might have failed.
    exit /b 1
)

copy "!JAR_FILE!" build\temp

if exist target\generated-resources (
    xcopy target\generated-resources\* build\temp /E /I
) else (
    echo Warning: No generated resources found. Proceeding without them.
)

jpackage ^
    --name "PictureComparerFX" ^
    --input build\temp ^
    --main-jar PictureComparerFX-0.7.0-SNAPSHOT-SHADED.jar ^
    --type msi ^
    --icon images\thumbnail.ico ^
    --main-class pl.magzik.picture_comparer_fx.Main ^
    --dest build\Windows
if %ERRORLEVEL% neq 0 (
    echo jpackage failed. Aborting.
    exit /b 1
)

rd /s /q build\temp

echo Build completed successfully.
exit /b 0

:check_command
where %1 > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo %1 is required but not installed. Aborting.
    exit /b 1
)
exit /b 0
