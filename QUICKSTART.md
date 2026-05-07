# 🚀 Guide de Démarrage Rapide

Bienvenue dans le Simulateur CPU ! Voici comment démarrer en 5 minutes.

## ⚡ Démarrage ultra-rapide (si Java est déjà installé)

```bash
# 1. Compiler le projet
build.bat                    # Windows
./build.sh                   # macOS/Linux

# 2. Exécuter le programme
java -jar target/cpu-simulator.jar

# 3. Exécuter les tests (optionnel)
mvn test                     # Si Maven est installé
```

## 🔧 Première installation (sans Java)

### Étape 1 : Installer Java 17+

**Windows :**
1. Téléchargez le JDK depuis : https://www.oracle.com/java/technologies/downloads/
2. Double-cliquez et installez (acceptez les paramètres par défaut)
3. Ouvrez Command Prompt et testez :
```cmd
java -version
```

**macOS :**
```bash
brew install openjdk@17
```

**Linux (Ubuntu) :**
```bash
sudo apt install openjdk-17-jdk
```

Pour des instructions détaillées, voir [INSTALLATION.md](INSTALLATION.md)

### Étape 2 : Valider la structure du projet

Windows :
```cmd
validate.bat
```

macOS/Linux :
```bash
chmod +x validate.sh
./validate.sh
```

### Étape 3 : Compiler et exécuter

**Avec le script de build (plus facile) :**

Windows :
```cmd
build.bat
java -jar target\cpu-simulator.jar
```

macOS/Linux :
```bash
./build.sh
java -jar target/cpu-simulator.jar
```

**Avec Maven (recommandé pour la production) :**

```bash
# Installer Maven si nécessaire (voir INSTALLATION.md)

# Compiler
mvn clean compile

# Exécuter les tests
mvn test

# Lancer l'application
mvn exec:java@run
```

## 📝 Exemples de Programs

### Exemple 1 : Addition simple

Modifiez `src/main/java/app/Main.java` :

```java
String programme =
    "LOAD_CONST r0, 10\n" +
    "LOAD_CONST r1, 20\n" +
    "ADD r2, r0, r1\n" +
    "STORE r2, @200\n" +
    "BREAK\n";
```

Recompilez et exécutez :
```bash
build.bat
java -jar target/cpu-simulator.jar
```

### Exemple 2 : Boucle simple

```java
String programme =
    "LOAD_CONST r0, 0\n" +
    "LOAD_CONST r1, 1\n" +
    "loop: ADD r0, r0, r1\n" +
    "LOAD_CONST r2, 5\n" +
    "BNE r0, r2, loop\n" +
    "BREAK\n";
```

### Exemple 3 : Manipulation de mémoire

```java
String programme =
    "LOAD_CONST r0, 42\n" +
    "STORE r0, @100\n" +
    "LOAD_MEM r1, @100\n" +
    "BREAK\n";
```

## 🧪 Exécuter les tests

Avec Maven :
```bash
mvn test
```

Avec JUnit (si compilés) :
```bash
java -cp target/classes:target/test-classes org.junit.platform.console.ConsoleLauncher --scan-classpath target/test-classes
```

## 📚 Documentation Complète

Pour une documentation plus détaillée :
- [README.md](README.md) - Documentation complète du projet
- [INSTALLATION.md](INSTALLATION.md) - Guide d'installation détaillé

## ❓ FAQ

**Q: Dois-je installer Maven ?**
A: Non, vous pouvez utiliser `build.bat` ou `build.sh`. Maven n'est optionnel que pour la gestion avancée des dépendances.

**Q: Puis-je modifier les programmes assembleur ?**
A: Oui ! Éditez la variable `programme` dans `src/main/java/app/Main.java` et recompilez.

**Q: Comment déboguer mon programme ?**
A: Ajoutez des `System.out.println()` dans `Main.java` pour afficher l'état du CPU et de la mémoire.

**Q: Où trouver mes résultats ?**
A: Les résultats s'affichent dans le terminal. Vérifiez aussi les emplacements mémoire que vous avez utilisés avec `STORE`.

## 🎯 Prochaines étapes

1. ✅ Installez Java
2. ✅ Compilez le projet
3. ✅ Exécutez le programme par défaut
4. 📝 Écrivez vos propres programmes assembleur
5. 🧪 Exécutez les tests pour vérifier votre compréhension

## 🆘 Besoin d'aide ?

- Vérifiez que Java 17+ est installé : `java -version`
- Relisez les commentaires dans le code source
- Consultez les tests pour des exemples d'utilisation
- Vérifiez la syntaxe de votre programme assembleur

---

**Bon développement ! 🚀**
