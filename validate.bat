@echo off
REM Script de validation de la structure du projet Simulateur CPU

setlocal enabledelayedexpansion

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║       Validation de la Structure du Projet                     ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

set errors=0

REM Fonction pour vérifier un fichier
setlocal enabledelayedexpansion

echo 📁 Structure des répertoires
echo ─────────────────────────────────────────────────────────────────

if exist "src" (echo ✅ src/) else (echo ❌ src/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\main" (echo ✅ src\main/) else (echo ❌ src\main/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\main\java" (echo ✅ src\main\java/) else (echo ❌ src\main\java/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\main\java\app" (echo ✅ src\main\java\app/) else (echo ❌ src\main\java\app/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\main\java\assembler" (echo ✅ src\main\java\assembler/) else (echo ❌ src\main\java\assembler/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\main\java\core" (echo ✅ src\main\java\core/) else (echo ❌ src\main\java\core/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\main\java\instruction" (echo ✅ src\main\java\instruction/) else (echo ❌ src\main\java\instruction/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\main\java\exception" (echo ✅ src\main\java\exception/) else (echo ❌ src\main\java\exception/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\test" (echo ✅ src\test/) else (echo ❌ src\test/ ^(MANQUANT^) & set /a errors+=1)
if exist "src\test\java" (echo ✅ src\test\java/) else (echo ❌ src\test\java/ ^(MANQUANT^) & set /a errors+=1)
echo.

echo 📄 Fichiers source principaux
echo ─────────────────────────────────────────────────────────────────

if exist "src\main\java\app\Main.java" (echo ✅ src\main\java\app\Main.java) else (echo ❌ src\main\java\app\Main.java & set /a errors+=1)
if exist "src\main\java\assembler\Assembler.java" (echo ✅ src\main\java\assembler\Assembler.java) else (echo ❌ src\main\java\assembler\Assembler.java & set /a errors+=1)
if exist "src\main\java\core\CPU.java" (echo ✅ src\main\java\core\CPU.java) else (echo ❌ src\main\java\core\CPU.java & set /a errors+=1)
if exist "src\main\java\core\ALU.java" (echo ✅ src\main\java\core\ALU.java) else (echo ❌ src\main\java\core\ALU.java & set /a errors+=1)
if exist "src\main\java\core\Memory.java" (echo ✅ src\main\java\core\Memory.java) else (echo ❌ src\main\java\core\Memory.java & set /a errors+=1)
if exist "src\main\java\instruction\Opcode.java" (echo ✅ src\main\java\instruction\Opcode.java) else (echo ❌ src\main\java\instruction\Opcode.java & set /a errors+=1)
echo.

echo 🧪 Fichiers de test
echo ─────────────────────────────────────────────────────────────────

if exist "src\test\java\ALUTest.java" (echo ✅ src\test\java\ALUTest.java) else (echo ❌ src\test\java\ALUTest.java & set /a errors+=1)
if exist "src\test\java\AssemblerTest.java" (echo ✅ src\test\java\AssemblerTest.java) else (echo ❌ src\test\java\AssemblerTest.java & set /a errors+=1)
if exist "src\test\java\CPUTest.java" (echo ✅ src\test\java\CPUTest.java) else (echo ❌ src\test\java\CPUTest.java & set /a errors+=1)
if exist "src\test\java\IntegrationTest.java" (echo ✅ src\test\java\IntegrationTest.java) else (echo ❌ src\test\java\IntegrationTest.java & set /a errors+=1)
echo.

echo 🔧 Configuration et scripts
echo ─────────────────────────────────────────────────────────────────

if exist "pom.xml" (echo ✅ pom.xml) else (echo ❌ pom.xml & set /a errors+=1)
if exist "build.sh" (echo ✅ build.sh) else (echo ❌ build.sh & set /a errors+=1)
if exist "build.bat" (echo ✅ build.bat) else (echo ❌ build.bat & set /a errors+=1)
if exist "README.md" (echo ✅ README.md) else (echo ❌ README.md & set /a errors+=1)
if exist "INSTALLATION.md" (echo ✅ INSTALLATION.md) else (echo ❌ INSTALLATION.md & set /a errors+=1)
echo.

echo 📋 Résumé
echo ─────────────────────────────────────────────────────────────────

if %errors% equ 0 (
    echo ✅ Toutes les vérifications sont passées !
    echo.
    echo Le projet est prêt à être compilé et exécuté.
    echo.
    echo Prochaines étapes :
    echo   1. Installer Java 17+ si ce n'est pas fait (voir INSTALLATION.md)
    echo   2. Compiler le projet avec : build.bat
    echo   3. Exécuter le programme avec : java -jar target\cpu-simulator.jar
) else (
    echo ❌ %errors% fichier(s) manquant(s)
)

echo.
pause
