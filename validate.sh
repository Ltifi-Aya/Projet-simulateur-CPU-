#!/bin/bash

# Script de validation de la structure du projet Simulateur CPU

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║       Validation de la Structure du Projet                     ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

errors=0
warnings=0

# Fonction pour vérifier un fichier
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✅${NC} $1"
    else
        echo -e "${RED}❌${NC} $1 (MANQUANT)"
        ((errors++))
    fi
}

# Fonction pour vérifier un répertoire
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✅${NC} $1/"
    else
        echo -e "${RED}❌${NC} $1/ (MANQUANT)"
        ((errors++))
    fi
}

echo "📁 Structure des répertoires"
echo "─────────────────────────────────────────────────────────────────"
check_dir "src"
check_dir "src/main"
check_dir "src/main/java"
check_dir "src/main/java/app"
check_dir "src/main/java/assembler"
check_dir "src/main/java/core"
check_dir "src/main/java/instruction"
check_dir "src/main/java/exception"
check_dir "src/test"
check_dir "src/test/java"
echo ""

echo "📄 Fichiers source principaux"
echo "─────────────────────────────────────────────────────────────────"
check_file "src/main/java/app/Main.java"
check_file "src/main/java/assembler/Assembler.java"
check_file "src/main/java/core/CPU.java"
check_file "src/main/java/core/ALU.java"
check_file "src/main/java/core/Memory.java"
check_file "src/main/java/core/RegisterFile.java"
check_file "src/main/java/core/ProgramCounter.java"
check_file "src/main/java/instruction/Opcode.java"
check_file "src/main/java/instruction/Instruction.java"
check_file "src/main/java/instruction/LoadConstInstruction.java"
check_file "src/main/java/instruction/LoadMemInstruction.java"
check_file "src/main/java/instruction/StoreInstruction.java"
check_file "src/main/java/instruction/AluInstruction.java"
check_file "src/main/java/instruction/JumpInstruction.java"
check_file "src/main/java/instruction/BeqInstruction.java"
check_file "src/main/java/instruction/BneInstruction.java"
check_file "src/main/java/instruction/BreakInstruction.java"
check_file "src/main/java/exception/InvalidOpcodeException.java"
check_file "src/main/java/exception/MemoryOutOfBoundsException.java"
check_file "src/main/java/exception/RegisterOutOfBoundsException.java"
echo ""

echo "🧪 Fichiers de test"
echo "─────────────────────────────────────────────────────────────────"
check_file "src/test/java/ALUTest.java"
check_file "src/test/java/MemoryTest.java"
check_file "src/test/java/RegisterFileTest.java"
check_file "src/test/java/AssemblerTest.java"
check_file "src/test/java/CPUTest.java"
check_file "src/test/java/IntegrationTest.java"
echo ""

echo "🔧 Configuration et scripts"
echo "─────────────────────────────────────────────────────────────────"
check_file "pom.xml"
check_file "build.sh"
check_file "build.bat"
check_file "README.md"
check_file "INSTALLATION.md"
echo ""

echo "📋 Résumé"
echo "─────────────────────────────────────────────────────────────────"

if [ $errors -eq 0 ]; then
    echo -e "${GREEN}✅ Toutes les vérifications sont passées !${NC}"
    echo ""
    echo "Le projet est prêt à être compilé et exécuté."
    echo ""
    echo "Prochaines étapes :"
    echo "  1. Installer Java 17+ si ce n'est pas fait (voir INSTALLATION.md)"
    echo "  2. Compiler le projet avec : mvn clean compile"
    echo "  3. Exécuter les tests avec : mvn test"
    echo "  4. Lancer le programme avec : mvn exec:java"
    exit 0
else
    echo -e "${RED}❌ $errors fichier(s) manquant(s)${NC}"
    exit 1
fi
