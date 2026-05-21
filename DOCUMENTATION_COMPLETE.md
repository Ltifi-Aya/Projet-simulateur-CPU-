# 📚 Documentation Complète - Simulateur CPU 8 bits

## 📑 Table des Matières
1. [Vue d'ensemble](#vue-densemble)
2. [Architecture du système](#architecture-du-système)
3. [L'Assembleur (Assembler.java)](#lassembleur)
4. [Le Processeur (CPU.java)](#le-processeur)
5. [Cycle Fetch/Decode/Execute](#cycle-fetchdecodeexecute)
6. [Les Instructions](#les-instructions)
7. [Exemple d'exécution](#exemple-dexécution)
8. [Guide d'utilisation](#guide-dutilisation)

---

## Vue d'ensemble

### Qu'est-ce qu'un simulateur de CPU ?

C'est un programme Java qui simule le fonctionnement d'un processeur 8 bits complet :
- **Mémoire** : 64 Ko (65536 octets) pour stocker les instructions et données
- **Registres** : 16 registres 8 bits (r0 à r15) pour les calculs rapides
- **ALU** : Unité arithmétique et logique pour les opérations (+, -, *, /, &, |, ^)
- **Assembleur** : Traduit un programme texte en bytecode exécutable

### Flux général

```
Programme texte (assembleur)
        ↓
    Assembleur
        ↓
  Bytecode en mémoire
        ↓
    CPU (Fetch/Decode/Execute)
        ↓
   Résultats en mémoire et registres
```

---

## Architecture du système

### Composants principaux

```
┌─────────────────────────────────────────┐
│          Main.java                      │
│  (Point d'entrée, lecture utilisateur)  │
└──────────────────┬──────────────────────┘
                   │
        ┌──────────┴──────────┐
        ↓                     ↓
   ┌─────────┐          ┌──────────┐
   │Assembler│          │   CPU    │
   └────┬────┘          └────┬─────┘
        │                    │
        └────────┬───────────┘
                 ↓
           ┌──────────────┐
           │   Memory     │
           │  (64 Ko)     │
           └──────────────┘
           
    Autres composants :
    ├─ RegisterFile (16 registres)
    ├─ ALU (opérations arithmétiques)
    ├─ ProgramCounter (PC)
    └─ Exceptions (gestion d'erreurs)
```

### Structure mémoire

```
Adresse (hexadécimal)   Contenu
┌──────────────────────────────────┐
│ 0x0000 - 0x000F │ Instructions   │ ← Programme chargé ici
│ 0x0010 - 0x00FF │ Données        │
│ 0x0100 - 0xFFFF │ Zone libre     │
└──────────────────────────────────┘
```

---

## L'Assembleur

### Rôle
Traduit un programme texte assembleur en bytecode (octets) stockés en mémoire.

### Fonctionnement en 2 passes

#### **Passe 1 : Conversion en octets**
```
Ligne assembleur      → Octets en mémoire
LOAD_CONST r0, 5     → [opcode] [r0] [5]    (3 octets)
LOAD_CONST r1, 6     → [opcode] [r1] [6]    (3 octets)
ADD r2, r0, r1       → [opcode] [r2] [r0] [r1] (4 octets)
```

#### **Passe 2 : Résolution des labels**
```
Les labels (comme "debut:", "boucle:") sont remplacés
par leurs adresses réelles en mémoire.
```

### Encodage des instructions

Chaque instruction occupe **3 ou 4 octets** selon son type :

```
LOAD_CONST rX, valeur  → [opcode:1] [registre:1] [valeur:1] = 3 octets

LOAD_MEM rX, @adresse  → [opcode:1] [registre:1] [addrH:1] [addrL:1] = 4 octets

STORE rX, @adresse     → [opcode:1] [registre:1] [addrH:1] [addrL:1] = 4 octets

ADD rX, rA, rB         → [opcode:1] [dest:1] [rA:1] [rB:1] = 4 octets

JUMP @adresse          → [opcode:1] [addrH:1] [addrL:1] = 3 octets
```

### Code de l'Assembleur - Méthodes clés

#### **emitByte(int value)**
```java
private void emitByte(int value) throws MemoryOutOfBoundsException {
    memory.writeByte(cursor, (byte) value);
    cursor++;  // Avance vers la case suivante
}
```
- Écrit 1 octet à l'adresse `cursor`
- Incrémente `cursor` pour la prochaine écriture

#### **emitWord(int address)**
```java
private void emitWord(int address) throws MemoryOutOfBoundsException {
    emitByte((address >> 8) & 0xFF);   // Octet fort
    emitByte(address & 0xFF);          // Octet faible
}
```
- Écrit une adresse 16 bits sur 2 octets
- Format big-endian (octet fort en premier)

#### **parseRegister(String token)**
```java
private int parseRegister(String token) {
    if (!token.startsWith("r")) {
        throw new IllegalArgumentException(...);
    }
    int index = Integer.parseInt(token.substring(1));
    if (index < 0 || index > 15) {
        throw new IllegalArgumentException(...);
    }
    return index;
}
```
- Convertit "r0" → 0, "r15" → 15
- Valide que le numéro est dans [0, 15]

#### **parseValue(String token)**
```java
private int parseValue(String token) {
    // Adresse : "@100" → 100
    if (token.startsWith("@")) {
        return parseValue(token.substring(1));
    }
    // Hexadécimal : "0x64" → 100
    if (token.startsWith("0x") || token.startsWith("0X")) {
        return Integer.parseInt(token.substring(2), 16);
    }
    // Décimal : "5" → 5
    return Integer.parseInt(token);
}
```
- Supporte 3 formats : décimal (5), adresse (@100), hexadécimal (0xFF)

### Exemple d'assemblage

```
Entrée utilisateur :
  Nombre 1 : 10
  Nombre 2 : 20

Programme généré :
  LOAD_CONST r0, 10     ← Charger 10 dans r0
  LOAD_CONST r1, 20     ← Charger 20 dans r1
  ADD r2, r0, r1        ← r2 = r0 + r1 = 30
  STORE r2, @200        ← Stocker r2 à l'adresse 200
  BREAK                 ← Arrêter l'exécution

Mémoire après assemblage :
  Adresse 0 : [opcode LOAD_CONST] [0] [10]
  Adresse 3 : [opcode LOAD_CONST] [1] [20]
  Adresse 6 : [opcode ADD] [2] [0] [1]
  Adresse 10: [opcode STORE] [2] [200 haut] [200 bas]
  Adresse 14: [opcode BREAK]
```

---

## Le Processeur

### Rôle
Exécute le programme chargé en mémoire selon le cycle **Fetch/Decode/Execute**.

### Composants du CPU

```java
public class CPU {
    private final Memory memory;              // Mémoire partagée
    private final RegisterFile registers;     // 16 registres
    private final ALU alu;                    // Unité arithmétique
    private final ProgramCounter pc;          // Compteur de programme
    private boolean running;                  // État d'exécution
}
```

### Méthodes clés

#### **fetch()**
```java
private int fetch() throws MemoryOutOfBoundsException {
    int value = memory.readByte(pc.get()) & 0xFF;
    pc.increment();  // Avance le PC
    return value;
}
```
- Lit l'octet à l'adresse du PC
- Incrémente le PC pour la prochaine lecture
- **Phase 1 du cycle Fetch/Decode/Execute**

#### **run()**
```java
public void run() throws InvalidOpcodeException,
        MemoryOutOfBoundsException, RegisterOutOfBoundsException {
    
    running = true;
    
    while (running) {
        // 1. FETCH
        int opcodeValue = fetch();
        
        // 2. DECODE
        Opcode opcode = Opcode.fromValue(opcodeValue);
        
        // 3. EXECUTE
        switch (opcode) {
            case LOAD_CONST:
                // Lecture et exécution
                int regDest = fetch();
                byte value = (byte) fetch();
                new LoadConstInstruction(regDest, value)
                    .execute(registers, memory, alu);
                break;
            // ... autres instructions ...
            case BREAK:
                running = false;
                break;
        }
    }
}
```

### État des registres

Après l'exécution de notre exemple (10 + 20) :

```
Registres :
  r0 = 10   (chargé par LOAD_CONST r0, 10)
  r1 = 20   (chargé par LOAD_CONST r1, 20)
  r2 = 30   (résultat de ADD r2, r0, r1)
  r3-r15 = 0 (non utilisés)

Mémoire[200] = 30  (stocké par STORE r2, @200)

PC = 15  (5 instructions × 3 bytes = 15)
```

---

## Cycle Fetch/Decode/Execute

C'est le cœur du processeur. Chaque instruction passe par 3 phases :

### Phase 1 : FETCH (Chercher)

```
PC = 0
↓
Lire l'octet à l'adresse 0 → [opcode LOAD_CONST = 10]
↓
PC = PC + 1 = 1
```

### Phase 2 : DECODE (Décoder)

```
opcode = 10
↓
fromValue(10) → Opcode.LOAD_CONST
↓
"C'est une instruction LOAD_CONST"
```

### Phase 3 : EXECUTE (Exécuter)

```
Opcode = LOAD_CONST
↓
Lire le registre destination    → fetch() → r0 (registre 0)
Lire la valeur constante        → fetch() → 10
↓
registers.set(0, 10)  ← r0 = 10
```

### Cycle complet visuel

```
┌─────────────────────────────────────────────┐
│         Boucle d'exécution (while running)  │
├─────────────────────────────────────────────┤
│                                             │
│  FETCH:                                     │
│  ┌─────────────────────────────────────┐   │
│  │ opcodeValue = memory[PC]            │   │
│  │ PC = PC + 1                         │   │
│  └─────────────────────────────────────┘   │
│                 ↓                           │
│  DECODE:                                    │
│  ┌─────────────────────────────────────┐   │
│  │ opcode = Opcode.fromValue(opcodeVal)│   │
│  └─────────────────────────────────────┘   │
│                 ↓                           │
│  EXECUTE:                                   │
│  ┌─────────────────────────────────────┐   │
│  │ switch(opcode) {                    │   │
│  │   case ADD: ... break;              │   │
│  │   case LOAD_CONST: ... break;       │   │
│  │   case BREAK: running = false;      │   │
│  │ }                                   │   │
│  └─────────────────────────────────────┘   │
│                                             │
└──────────────┬────────────────────────────┬─┘
               │                            │
            OUI │                            │ NON (running=false)
               ↓                            ↓
           [Boucle]                    [Arrêt]
```

---

## Les Instructions

### Format général

Chaque instruction possède un **opcode** (code d'opération) qui l'identifie.

### Instructions disponibles

#### **1. LOAD_CONST (Charger une constante)**
```
Syntaxe : LOAD_CONST rX, valeur
Exemple : LOAD_CONST r0, 5

Opcode : 0x0A (10)
Format :  [opcode:1] [registre:1] [valeur:1]
          [0x0A]     [0]          [5]

Effet : r0 = 5
Cycle : 3 octets
```

#### **2. LOAD_MEM (Charger depuis la mémoire)**
```
Syntaxe : LOAD_MEM rX, @adresse
Exemple : LOAD_MEM r1, @200

Opcode : 0x0B (11)
Format :  [opcode:1] [registre:1] [addrH:1] [addrL:1]
          [0x0B]     [1]          [0]      [200]

Effet : r1 = memory[200]
Cycle : 4 octets
```

#### **3. STORE (Stocker en mémoire)**
```
Syntaxe : STORE rX, @adresse
Exemple : STORE r2, @200

Opcode : 0x0C (12)
Format :  [opcode:1] [registre:1] [addrH:1] [addrL:1]
          [0x0C]     [2]          [0]      [200]

Effet : memory[200] = r2
Cycle : 4 octets
```

#### **4. ADD (Addition)**
```
Syntaxe : ADD rDest, rA, rB
Exemple : ADD r2, r0, r1

Opcode : 0x01
Format :  [opcode:1] [dest:1] [rA:1] [rB:1]
          [0x01]     [2]      [0]    [1]

Effet : rDest = rA + rB
Cycle : 4 octets
```

#### **5. SUB (Soustraction)**
```
Syntaxe : SUB rDest, rA, rB
Exemple : SUB r2, r0, r1

Opcode : 0x02
Effet : rDest = rA - rB
```

#### **6. MUL (Multiplication)**
```
Syntaxe : MUL rDest1, rDest2, rA, rB
Exemple : MUL r3, r4, r0, r1

Opcode : 0x05
Format :  [opcode:1] [dest1:1] [dest2:1] [rA:1] [rB:1]
          [0x05]     [3]       [4]       [0]    [1]

Effet : rDest1:rDest2 = rA * rB (résultat 16 bits)
Cycle : 5 octets
```

#### **7. DIV (Division)**
```
Syntaxe : DIV rDest1, rDest2, rA, rB
Exemple : DIV r3, r4, r0, r1

Opcode : 0x06
Effet : rDest1 = rA / rB, rDest2 = rA % rB
Cycle : 5 octets
```

#### **8. AND (ET logique)**
```
Syntaxe : AND rDest, rA, rB
Exemple : AND r2, r0, r1

Opcode : 0x07
Effet : rDest = rA & rB (bit à bit)
```

#### **9. OR (OU logique)**
```
Syntaxe : OR rDest, rA, rB
Opcode : 0x08
Effet : rDest = rA | rB (bit à bit)
```

#### **10. XOR (OU exclusif)**
```
Syntaxe : XOR rDest, rA, rB
Opcode : 0x09
Effet : rDest = rA ^ rB (bit à bit)
```

#### **11. JUMP (Saut inconditionnel)**
```
Syntaxe : JUMP @adresse
Exemple : JUMP @10

Opcode : 0x0D (13)
Format :  [opcode:1] [addrH:1] [addrL:1]
          [0x0D]     [0]       [10]

Effet : PC = adresse
Cycle : 3 octets
```

#### **12. BEQ (Branch if Equal)**
```
Syntaxe : BEQ rA, rB, @adresse
Exemple : BEQ r0, r1, @20

Opcode : 0x0E (14)
Effet : Si rA == rB, alors PC = adresse
Cycle : 5 octets
```

#### **13. BNE (Branch if Not Equal)**
```
Syntaxe : BNE rA, rB, @adresse
Exemple : BNE r0, r1, @20

Opcode : 0x0F (15)
Effet : Si rA != rB, alors PC = adresse
Cycle : 5 octets
```

#### **14. BREAK (Arrêt)**
```
Syntaxe : BREAK

Opcode : 0x00 (0)
Format :  [opcode:1]
          [0x00]

Effet : running = false (arrête la boucle)
Cycle : 1 octet
```

---

## Exemple d'exécution

### Programme source
```
Entrée utilisateur :
  Nombre 1 : 10
  Nombre 2 : 20

Programme généré :
  LOAD_CONST r0, 10
  LOAD_CONST r1, 20
  ADD r2, r0, r1
  STORE r2, @200
  BREAK
```

### Étape par étape

#### **Étape 1 : Assemblage**

L'Assembleur traduit le programme en octets :

```
Adresse | Octet
--------|------
0       | 0x0A (LOAD_CONST)
1       | 0x00 (registre r0)
2       | 10   (valeur)
--------|------
3       | 0x0A (LOAD_CONST)
4       | 0x01 (registre r1)
5       | 20   (valeur)
--------|------
6       | 0x01 (ADD)
7       | 0x02 (registre r2)
8       | 0x00 (registre r0)
9       | 0x01 (registre r1)
--------|------
10      | 0x0C (STORE)
11      | 0x02 (registre r2)
12      | 0x00 (adresse haute = 200 >> 8 = 0)
13      | 200  (adresse basse = 200 & 0xFF = 200)
--------|------
14      | 0x00 (BREAK)
```

#### **Étape 2 : Exécution (Fetch/Decode/Execute)**

**Cycle 1 (PC = 0):**
```
FETCH:   opcodeValue = memory[0] = 0x0A
         PC = 0 + 1 = 1
         
DECODE:  0x0A → Opcode.LOAD_CONST
         
EXECUTE: regDest = fetch() = memory[1] = 0x00 (r0)
                   PC = 2
         value = fetch() = memory[2] = 10
                   PC = 3
         registers.set(0, 10)
         
État :   r0 = 10, PC = 3
```

**Cycle 2 (PC = 3):**
```
FETCH:   opcodeValue = memory[3] = 0x0A
         PC = 4
         
DECODE:  0x0A → Opcode.LOAD_CONST
         
EXECUTE: regDest = fetch() = memory[4] = 0x01 (r1)
                   PC = 5
         value = fetch() = memory[5] = 20
                   PC = 6
         registers.set(1, 20)
         
État :   r0 = 10, r1 = 20, PC = 6
```

**Cycle 3 (PC = 6):**
```
FETCH:   opcodeValue = memory[6] = 0x01
         PC = 7
         
DECODE:  0x01 → Opcode.ADD
         
EXECUTE: dest = fetch() = memory[7] = 0x02 (r2)
                PC = 8
         rA = fetch() = memory[8] = 0x00 (r0)
              PC = 9
         rB = fetch() = memory[9] = 0x01 (r1)
              PC = 10
         alu.add(10, 20) → 30
         registers.set(2, 30)
         
État :   r0 = 10, r1 = 20, r2 = 30, PC = 10
```

**Cycle 4 (PC = 10):**
```
FETCH:   opcodeValue = memory[10] = 0x0C
         PC = 11
         
DECODE:  0x0C → Opcode.STORE
         
EXECUTE: regSrc = fetch() = memory[11] = 0x02 (r2)
                   PC = 12
         storeH = fetch() = memory[12] = 0x00
                   PC = 13
         storeL = fetch() = memory[13] = 200
                   PC = 14
         storeAddr = (0 << 8) | 200 = 200
         memory.writeByte(200, 30)
         
État :   memory[200] = 30, PC = 14
```

**Cycle 5 (PC = 14):**
```
FETCH:   opcodeValue = memory[14] = 0x00
         PC = 15
         
DECODE:  0x00 → Opcode.BREAK
         
EXECUTE: running = false → Arrêt de la boucle

État final : running = false
```

### Résultat final

```
Registres :
  r0 = 10
  r1 = 20
  r2 = 30
  r3-r15 = 0

Mémoire[200] = 30

PC = 15
```

---

## Guide d'utilisation

### Compilation et exécution

#### **Avec Maven**
```bash
cd "C:\Users\farah\OneDrive\Bureau\Projet-simulateur-CPU"
mvn clean compile
mvn exec:java@run
```

#### **Avec le script batch (Windows)**
```bash
build.bat
java -jar target/cpu-simulator.jar
```

### Interaction avec le programme

1. **Lancement :**
```powershell
mvn exec:java@run
```

2. **Le programme demande 2 nombres :**
```
Entrez le premier nombre (0-255) : 
```

3. **Entrez votre nombre :**
```
10
```

4. **Entrez le deuxième nombre :**
```
Entrez le deuxième nombre (0-255) : 
20
```

5. **Résultats affichés :**
```
=== État final ===
r0          = 10
r1          = 20
r2          = 30
mémoire[200]= 30
PC final    = 15
```

### Créer vos propres programmes

Modifiez [Main.java](src/main/java/app/Main.java) pour changer le programme :

```java
// Remplacez cette partie :
String programme =
    "LOAD_CONST r0, " + num1 + "\n" +
    "LOAD_CONST r1, " + num2 + "\n" +
    "ADD r2, r0, r1\n" +
    "STORE r2, @200\n" +
    "BREAK\n";

// Par votre programme personnalisé, par exemple :
String programme =
    "LOAD_CONST r0, " + num1 + "\n" +    // Charger num1 dans r0
    "LOAD_CONST r1, " + num2 + "\n" +    // Charger num2 dans r1
    "SUB r2, r0, r1\n" +                  // r2 = r0 - r1
    "STORE r2, @250\n" +                  // Stocker en mémoire[250]
    "BREAK\n";
```

### Exemple : Multiplication

```java
String programme =
    "LOAD_CONST r0, " + num1 + "\n" +
    "LOAD_CONST r1, " + num2 + "\n" +
    "MUL r2, r3, r0, r1\n" +  // r2:r3 = r0 * r1
    "STORE r2, @200\n" +
    "STORE r3, @201\n" +
    "BREAK\n";
```

### Exemple : Boucle avec BEQ

```java
String programme =
    "LOAD_CONST r0, 10\n" +        // r0 = 10 (compteur)
    "LOAD_CONST r1, 0\n" +         // r1 = 0 (accumulateur)
    "LOAD_CONST r15, 10\n" +       // r15 = 10 (condition)
    "BEQ r0, r15, @14\n" +         // Si r0 == 10, sauter à @14
    "ADD r1, r1, r0\n" +           // r1 = r1 + r0
    "SUB r0, r0, @1\n" +           // r0 = r0 - 1
    "JUMP @6\n" +                  // Retourner au test
    "STORE r1, @200\n" +           // Stocker le résultat
    "BREAK\n";
```

---

## Résumé des concepts clés

| Concept | Explication |
|---------|------------|
| **Assembleur** | Traduit le texte en octets exécutables |
| **Fetch** | Lit l'instruction actuelle en mémoire |
| **Decode** | Identifie le type d'instruction |
| **Execute** | Exécute l'instruction |
| **PC** | Pointe l'adresse de la prochaine instruction |
| **Registre** | Stockage rapide (16 registres × 8 bits) |
| **Mémoire** | Stockage grande capacité (64 Ko) |
| **ALU** | Effectue les opérations mathématiques |
| **Opcode** | Code numérique identifiant une instruction |

---

## Fichiers du projet

```
src/main/java/
├─ app/Main.java                 ← Point d'entrée
├─ assembler/Assembler.java      ← Traducteur assembleur
├─ core/
│  ├─ CPU.java                   ← Processeur (Fetch/Decode/Execute)
│  ├─ Memory.java                ← 64 Ko de mémoire
│  ├─ RegisterFile.java          ← 16 registres
│  ├─ ALU.java                   ← Unité arithmétique
│  └─ ProgramCounter.java        ← Compteur de programme
├─ instruction/
│  ├─ Opcode.java                ← Énumération des codes d'opération
│  ├─ Instruction.java           ← Interface base
│  ├─ LoadConstInstruction.java  ← LOAD_CONST
│  ├─ AluInstruction.java        ← ADD, SUB, MUL, DIV, AND, OR, XOR
│  ├─ JumpInstruction.java       ← JUMP
│  ├─ BeqInstruction.java        ← BEQ
│  ├─ BneInstruction.java        ← BNE
│  ├─ StoreInstruction.java      ← STORE
│  └─ ...
└─ exception/
   ├─ InvalidOpcodeException.java
   ├─ MemoryOutOfBoundsException.java
   └─ RegisterOutOfBoundsException.java

src/test/java/
├─ CPUTest.java
├─ AssemblerTest.java
├─ ALUTest.java
└─ ...
```

---

**Fin de la documentation**
