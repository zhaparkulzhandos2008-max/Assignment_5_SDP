@echo off
cd /d "%~dp0"
if not exist target\classes mkdir target\classes
(for /r src\main\java %%f in (*.java) do @echo "%%f") > target\sources.txt
javac --release 17 -encoding UTF-8 -d target\classes @target\sources.txt
if errorlevel 1 exit /b 1
java -cp target\classes kz.aitu.delivery.Main
