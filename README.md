# 🖥️ Simulateur de Processeur 8 bits

Un simulateur complet de processeur 8 bits développé en Java avec assembleur intégré, mémoire 64 Ko, 16 registres, ALU complète, et cycle Fetch/Decode/Execute.

## 📋 Caractéristiques

- ✅ **Assembleur textuel** : Traduit du code assembleur en bytecode
- ✅ **Mémoire 64 Ko** : Adressable sur 16 bits
- ✅ **16 registres 8 bits** : r0 à r15
- ✅ **ALU complète** : Addition, soustraction, multiplication, division, ET, OU, OU exclusif
- ✅ **Instructions de branchement** : JUMP, BEQ (Branch if Equal), BNE (Branch if Not Equal)
- ✅ **Instructions de mémoire** : LOAD_MEM, STORE
- ✅ **Gestion des labels** : Support des labels nommés pour les sauts
- ✅ **Tests unitaires complets** : ALU, Mémoire, Registres, Assembleur, CPU, Intégration

## 📦 Prérequis

- **Java 17 ou supérieur**
- **Maven 3.6+ (optionnel)** - utilisé pour la compilation et les tests
- **Bash ou PowerShell** - pour les scripts de build

## 🚀 Installation & Compilation

### Option 1: Avec Maven (recommandé)

```bash
mvn clean compile
mvn test
mvn exec:java@run
```

### Option 2: Avec le script de build

**Sur Windows :**
```bash
build.bat
java -jar target/cpu-simulator.jar
```

**Sur Linux/Mac :**
```bash
chmod +x build.sh
./build.sh
java -jar target/cpu-simulator.jar
```

### Option 3: Compilation manuelle avec javac

```bash
# Créer les répertoires
mkdir -p target/classes

# Compiler les sources
javac -d target/classes -encoding UTF-8 src/main/java/**/*.java

# Exécuter
java -cp target/classes app.Main
```

## 📚 Architecture du Projet

```
src/
├── main/java/
│   ├── app/
│   │   └── Main.java              # Point d'entrée principal
│   ├── assembler/
│   │   └── Assembler.java         # Traduction assembleur → bytecode
│   ├── core/
│   │   ├── CPU.java              # Processeur (Fetch/Decode/Execute)
│   │   ├── ALU.java              # Unité arithmétique/logique
│   │   ├── Memory.java           # Mémoire 64 Ko
│   │   ├── RegisterFile.java     # 16 registres 8-bit
│   │   ├── ProgramCounter.java   # Compteur de programme
│   │   └── Instruction.java      # Classes d'instructions
│   ├── instruction/
│   │   ├── Opcode.java           # Énumération des opcodes
│   │   ├── LoadConstInstruction.java
│   │   ├── AluInstruction.java
│   │   ├── JumpInstruction.java
│   │   ├── BeqInstruction.java
│   │   └── ...autres instructions...
│   └── exception/
│       ├── InvalidOpcodeException.java
│       ├── MemoryOutOfBoundsException.java
│       └── RegisterOutOfBoundsException.java
├── test/java/
│   ├── ALUTest.java              # Tests ALU
│   ├── MemoryTest.java           # Tests Mémoire
│   ├── RegisterFileTest.java     # Tests Registres
│   ├── AssemblerTest.java        # Tests Assembleur ✨ NOUVEAU
│   ├── CPUTest.java              # Tests CPU ✨ NOUVEAU
│   └── IntegrationTest.java      # Tests d'intégration ✨ NOUVEAU
└── pom.xml                         # Configuration Maven
```

## 🛠️ Instructions Supportées

| Instruction | Syntaxe | Octets | Description |
|------------|---------|--------|-------------|
| **LOAD_CONST** | `LOAD_CONST r0, 5` | 3 | Charge une constante dans un registre |
| **LOAD_MEM** | `LOAD_MEM r0, @100` | 4 | Charge une valeur de la mémoire |
| **STORE** | `STORE r0, @100` | 4 | Stocke un registre en mémoire |
| **ADD** | `ADD r2, r0, r1` | 4 | Addition : r2 = r0 + r1 |
| **SUB** | `SUB r2, r0, r1` | 4 | Soustraction : r2 = r0 - r1 |
| **MUL** | `MUL r2, r3, r0, r1` | 5 | Multiplication : r2:r3 = r0 × r1 |
| **DIV** | `DIV r2, r3, r0, r1` | 5 | Division : r2 = r0 ÷ r1, r3 = reste |
| **AND** | `AND r2, r0, r1` | 4 | ET logique : r2 = r0 AND r1 |
| **OR** | `OR r2, r0, r1` | 4 | OU logique : r2 = r0 OR r1 |
| **XOR** | `XOR r2, r0, r1` | 4 | OU exclusif : r2 = r0 XOR r1 |
| **JUMP** | `JUMP @100` | 3 | Saut inconditionnelà l'adresse |
| **BEQ** | `BEQ r0, r1, @100` | 5 | Saute si r0 == r1 |
| **BNE** | `BNE r0, r1, @100` | 5 | Saute si r0 != r1 |
| **BREAK** | `BREAK` | 1 | Arrête l'exécution |

## 💡 Exemple de Programme

```java
// Dans Main.java ou dans un String
String programme = """
    LOAD_CONST r0, 5      // r0 = 5
    LOAD_CONST r1, 6      // r1 = 6
    ADD r2, r0, r1        // r2 = 11
    STORE r2, @200        // mémoire[200] = r2
    BREAK
""";

// Assemblage et exécution
Memory memory = new Memory();
Assembler assembler = new Assembler(memory);
assembler.assemble(programme);

CPU cpu = new CPU(memory);
cpu.run();

// Affichage des résultats
System.out.println("r0 = " + cpu.getRegister(0));     // 5
System.out.println("r1 = " + cpu.getRegister(1));     // 6
System.out.println("r2 = " + cpu.getRegister(2));     // 11
System.out.println("Mémoire[200] = " + memory.readByte(200)); // 11
```

## 🧪 Tests Unitaires

### Exécuter tous les tests avec Maven

```bash
mvn test
```

### Tests disponibles

- **ALUTest.java** : Opérations arithmétiques et logiques
- **MemoryTest.java** : Lecture/écriture mémoire, gestion des limites
- **RegisterFileTest.java** : Opérations sur les registres
- **AssemblerTest.java** : ✨ Parsing d'instructions, gestion des labels
- **CPUTest.java** : ✨ Fetch/Decode/Execute, instructions individuelles
- **IntegrationTest.java** : ✨ Programmes complets assembleur + CPU

## 📝 Syntaxe de l'Assembleur

### Formats de valeurs

```
Décimal    : LOAD_CONST r0, 42
Hexadécimal: LOAD_CONST r0, 0xFF
Adresse    : LOAD_MEM r0, @256
            ou avec hex: LOAD_MEM r0, @0x100
```

### Labels et branchements

```
    LOAD_CONST r0, 0
loop: ADD r0, r0, r3        // Label "loop"
    LOAD_CONST r1, 10
    BNE r0, r1, loop        // Saute au label "loop"
    BREAK
```

### Commentaires

```
LOAD_CONST r0, 5  // Ceci est un commentaire
```

## 🐛 Débogage

Pour déboguer un programme, vous pouvez ajouter des affichages dans Main.java:

```java
System.out.println("PC = " + cpu.getPC());
System.out.println("r0 = " + (cpu.getRegister(0) & 0xFF));
System.out.println("Mémoire[300] = " + (memory.readByte(300) & 0xFF));
```

## 📊 État du Projet

| Composant | Statut | Tests |
|-----------|--------|-------|
| ALU | ✅ Complet | ✅ ALUTest.java |
| Mémoire | ✅ Complet | ✅ MemoryTest.java |
| Registres | ✅ Complet | ✅ RegisterFileTest.java |
| Assembleur | ✅ Complet | ✅ AssemblerTest.java |
| CPU | ✅ Complet | ✅ CPUTest.java |
| Intégration | ✅ Complet | ✅ IntegrationTest.java |
| Main | ✅ Complet | ✅ Exécutable |

## 👨‍💻 Développement

### Ajouter une nouvelle instruction

1. Ajouter un nouvel opcode dans [instruction/Opcode.java](instruction/Opcode.java)
2. Créer une classe d'instruction dans [instruction/](instruction/)
3. Ajouter le parsing dans [assembler/Assembler.java](assembler/Assembler.java)
4. Ajouter le décodage dans [core/CPU.java](core/CPU.java)
5. Ajouter des tests dans [CPUTest.java](src/test/java/CPUTest.java)

## 📄 Licence

Ce projet est fourni à titre éducatif.

## 📞 Support

Pour toute question ou problème :
1. Consultez la documentation du code source
2. Vérifiez les tests existants pour des exemples
3. Assurez-vous que Java 17+ est installé
