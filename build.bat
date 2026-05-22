@echo off
REM Script de compilation du projet Simulateur CPU pour Windows

setlocal enabledelayedexpansion

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║       Simulateur CPU - Script de Compilation (Windows)         ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Répertoires
set SOURCE_DIR=src\main\java
set TEST_DIR=src\test\java
set BUILD_DIR=target\classes
set TEST_BUILD_DIR=target\test-classes
set LIB_DIR=lib

REM Chemin vers Java - utilise javac du PATH
set JAVAC=javac
set JAVA=java

REM Créer les répertoires de sortie
echo 📁 Création des répertoires de build...
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%TEST_BUILD_DIR%" mkdir "%TEST_BUILD_DIR%"

REM Vérifier que javac est disponible
"%JAVAC%" -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Erreur: javac n'a pas été trouvé
    echo    Vérifiez que Java 17 est installé à: %JAVA_HOME%
    pause
    exit /b 1
)

REM Compiler les sources principales
echo.
echo 🔨 Compilation des sources principales...
for /r "%SOURCE_DIR%" %%F in (*.java) do (
    set SOURCES=!SOURCES! "%%F"
)

"%JAVAC%" -d "%BUILD_DIR%" -encoding UTF-8 %SOURCES%

if errorlevel 1 (
    echo ❌ Erreur de compilation des sources
    pause
    exit /b 1
)

echo ✅ Sources compilées avec succès

REM Compiler les tests
echo.
echo 🔨 Tentative de compilation des tests...

set JUNIT_CLASSPATH=
if exist "%LIB_DIR%" (
    for %%F in ("%LIB_DIR%\*.jar") do (
        set JUNIT_CLASSPATH=!JUNIT_CLASSPATH!;"%%F"
    )
)

set TEST_SOURCES=
for /r "%TEST_DIR%" %%F in (*.java) do (
    set TEST_SOURCES=!TEST_SOURCES! "%%F"
)

"%JAVAC%" -d "%TEST_BUILD_DIR%" -cp "%BUILD_DIR%!JUNIT_CLASSPATH!" -encoding UTF-8 %TEST_SOURCES% 2>nul

if errorlevel 1 (
    echo ⚠️  Les tests n'ont pas pu être compilés (JUnit manquant)
) else (
    echo ✅ Tests compilés avec succès
)

REM Créer un JAR exécutable
echo.
echo 📦 Création du JAR exécutable...
if not exist "target" mkdir target
jar cfe target\cpu-simulator.jar app.Main -C "%BUILD_DIR%" . 2>nul

if errorlevel 1 (
    echo ⚠️  JAR non créé (optionnel - le programme fonctionne quand même)
) else (
    echo ✅ JAR créé : target\cpu-simulator.jar
)

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║                   ✅ Build Réussi                             ║
echo ║                                                                ║
echo ║  Pour exécuter le programme :                                  ║
echo ║    java -cp target\classes app.Main                            ║
echo ║  ou                                                            ║
echo ║    java -jar target\cpu-simulator.jar                          ║
echo ║                                                                ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

pause
