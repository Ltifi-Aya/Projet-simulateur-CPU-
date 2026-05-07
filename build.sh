#!/bin/bash

# Script de compilation du projet Simulateur CPU

set -e  # Exit on error

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║       Simulateur CPU - Script de Compilation                   ║"
echo "╚════════════════════════════════════════════════════════════════╝"

# Répertoires
SOURCE_DIR="src/main/java"
TEST_DIR="src/test/java"
BUILD_DIR="target/classes"
TEST_BUILD_DIR="target/test-classes"
LIB_DIR="lib"

# Créer les répertoires de sortie
echo "📁 Création des répertoires de build..."
mkdir -p "$BUILD_DIR"
mkdir -p "$TEST_BUILD_DIR"

# Télécharger JUnit si nécessaire
echo "📦 Vérification des dépendances JUnit..."
if [ ! -d "$LIB_DIR" ]; then
    mkdir -p "$LIB_DIR"
    echo "  - Téléchargement de JUnit 5..."
    # Télécharger les JARs JUnit (cela dépend de la connexion internet)
    echo "  ⚠️  Veuillez télécharger JUnit manuellement"
    echo "  Visitez: https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter"
fi

# Compiler les sources principales
echo "🔨 Compilation des sources principales..."
javac -d "$BUILD_DIR" \
    -encoding UTF-8 \
    $(find "$SOURCE_DIR" -name "*.java" | tr '\n' ' ')

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation des sources"
    exit 1
fi

echo "✅ Sources compilées avec succès"

# Compiler les tests (si JUnit est disponible)
echo "🔨 Tentative de compilation des tests..."
JUNIT_CLASSPATH=""
if [ -d "$LIB_DIR" ]; then
    for jar in "$LIB_DIR"/*.jar; do
        JUNIT_CLASSPATH="$JUNIT_CLASSPATH:$jar"
    done
fi

javac -d "$TEST_BUILD_DIR" \
    -cp "$BUILD_DIR:$JUNIT_CLASSPATH" \
    -encoding UTF-8 \
    $(find "$TEST_DIR" -name "*.java" | tr '\n' ' ') 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ Tests compilés avec succès"
else
    echo "⚠️  Les tests n'ont pas pu être compilés (JUnit manquant)"
fi

# Créer un JAR exécutable
echo "📦 Création du JAR exécutable..."
jar cfe target/cpu-simulator.jar app.Main -C "$BUILD_DIR" .
echo "✅ JAR créé : target/cpu-simulator.jar"

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                   ✅ Build Réussi                             ║"
echo "║                                                                ║"
echo "║  Pour exécuter le programme :                                  ║"
echo "║    java -cp target/classes app.Main                            ║"
echo "║  ou                                                            ║"
echo "║    java -jar target/cpu-simulator.jar                          ║"
echo "║                                                                ║"
echo "║  Pour exécuter les tests (si compilés) :                       ║"
echo "║    java -cp target/classes:target/test-classes org.junit.platform.console.ConsoleLauncher --scan-classpath target/test-classes"
echo "║                                                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
