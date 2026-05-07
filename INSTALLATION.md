# 🛠️ Guide d'Installation Java

Ce document explique comment installer Java pour pouvoir compiler et exécuter le projet Simulateur CPU.

## 💻 Système Windows

### Étape 1 : Télécharger le JDK

1. Ouvrez votre navigateur et allez sur : https://www.oracle.com/java/technologies/downloads/
2. Sélectionnez **Java 17 LTS** (Long Term Support) ou version plus récente
3. Cliquez sur le fichier Windows x64 (par ex. `jdk-17_windows-x64_bin.exe`)
4. Acceptez les conditions et téléchargez

### Étape 2 : Installer le JDK

1. Double-cliquez sur le fichier téléchargé (`.exe`)
2. Cliquez sur **Next** pour accepter les paramètres par défaut
3. Notez le chemin d'installation (par défaut : `C:\Program Files\Java\jdk-17`)
4. Complétez l'installation

### Étape 3 : Configurer les variables d'environnement

#### Sur Windows 10/11 :

1. Appuyez sur `Win + X` et sélectionnez **Paramètres du système avancés**
2. Allez à l'onglet **Avancé** → **Variables d'environnement**
3. Cliquez sur **Nouvelle...** (sous Variables système)
4. Créez une nouvelle variable :
   - Nom : `JAVA_HOME`
   - Valeur : `C:\Program Files\Java\jdk-17` (ou votre chemin d'installation)

5. Éditez la variable système `Path` :
   - Cliquez sur `Path` → **Modifier**
   - Cliquez sur **Nouveau**
   - Ajoutez : `%JAVA_HOME%\bin`
   - Cliquez sur **OK** partout

### Étape 4 : Vérifier l'installation

Ouvrez PowerShell ou Command Prompt et tapez :
```powershell
java -version
javac -version
```

Vous devriez voir quelque chose comme :
```
java version "17.0.x" 2021-09-14 LTS
Java(TM) SE Runtime Environment (build 17.0.x+8-LTS-39)
```

## 🍎 Système macOS

### Avec Homebrew (recommandé)

```bash
# Installer Homebrew si vous ne l'avez pas
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Installer Java
brew install openjdk@17

# Lier Java au système
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Vérifier l'installation
java -version
javac -version
```

### Téléchargement manuel

1. Allez sur https://www.oracle.com/java/technologies/downloads/
2. Téléchargez le fichier macOS
3. Double-cliquez sur le `.dmg` et suivez les instructions
4. Vérifiez : `java -version`

## 🐧 Système Linux (Ubuntu/Debian)

```bash
# Mettre à jour les packages
sudo apt update

# Installer OpenJDK 17
sudo apt install openjdk-17-jdk -y

# Vérifier l'installation
java -version
javac -version
```

### Pour d'autres distributions Linux

**Fedora/RHEL :**
```bash
sudo dnf install java-17-openjdk-devel -y
```

**Arch Linux :**
```bash
sudo pacman -S jdk-openjdk
```

## 🚀 Après Installation

Une fois Java installé, vous pouvez compiler et exécuter le projet :

### Avec Maven (recommandé)

```bash
# Installer Maven si vous ne l'avez pas
# Windows : https://maven.apache.org/download.cgi
# macOS : brew install maven
# Linux : sudo apt install maven

# Compiler et tester
mvn clean compile
mvn test

# Exécuter
mvn exec:java
```

### Avec le script de build

**Windows :**
```cmd
build.bat
java -jar target\cpu-simulator.jar
```

**macOS/Linux :**
```bash
chmod +x build.sh
./build.sh
java -jar target/cpu-simulator.jar
```

### Avec javac directement

```bash
mkdir -p target/classes
javac -d target/classes -encoding UTF-8 src/main/java/**/*.java
java -cp target/classes app.Main
```

## ✅ Vérification

Si tout est bien installé, ces commandes devraient fonctionner :

```bash
java -version    # Affiche la version de Java
javac -version   # Affiche la version du compilateur
mvn -version     # (optionnel) Affiche la version de Maven
```

## 🆘 Dépannage

### "java command not found"
- Vérifiez que `JAVA_HOME` est correctement défini
- Redémarrez votre terminal après l'installation
- Vérifiez le chemin d'installation du JDK

### "javac command not found"
- Vous avez installé JRE au lieu de JDK
- Désinstallez et réinstallez le **JDK** (Development Kit) complet

### "Permission denied" (macOS/Linux)
- Utilisez `chmod +x build.sh` avant de lancer le script

### Erreur de version
- Assurez-vous que vous avez Java **17 ou supérieur**
- Mettez à jour Java vers une version plus récente

## 📚 Ressources

- [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/)
- [OpenJDK](https://openjdk.java.net/)
- [Maven Installation Guide](https://maven.apache.org/install.html)

