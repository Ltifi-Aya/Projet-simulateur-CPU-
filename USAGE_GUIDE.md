# Guide d'Utilisation du Simulateur CPU

## 📋 Vue d'ensemble

Le simulateur permet de :
1. **Saisir** un programme assembleur
2. **Assembler** le programme en instructions binaires
3. **Exécuter** le programme
4. **Inspecter** l'état des registres et de la mémoire

---

## 🔄 Flux de travail complet

### Étape 1 : Saisir un programme
**Menu : [1] Saisir un programme assembleur**

```
Instructions disponibles :
- load r<N>, <valeur>      Charger une constante dans un registre
- load r<N>, @<adresse>    Charger une valeur depuis la mémoire
- store r<N>, <adresse>    Stocker un registre en mémoire
- add r<dst>, r<src1>, r<src2>     Addition
- sub r<dst>, r<src1>, r<src2>     Soustraction
- mul r<dst>, r<src1>, r<src2>     Multiplication
- div r<dst>, r<src1>, r<src2>     Division
- and r<dst>, r<src1>, r<src2>     ET binaire
- or r<dst>, r<src1>, r<src2>      OU binaire
- xor r<dst>, r<src1>, r<src2>     OU exclusif
- jump <adresse>           Saut inconditionnel
- beq r<src1>, r<src2>, <adresse>  Saut si égal
- bne r<src1>, r<src2>, <adresse>  Saut si non égal
- break                    Arrêter l'exécution
```

**Exemple :**
```
load r0,1
load r1,2
add r2,r0,r1
break
```
Terminez par `FIN`

---

### Étape 2 : Assembler le programme
**Menu : [2] Assembler et charger le programme en memoire**

Le programme est converti en instructions binaires et chargé en mémoire à l'adresse 0.

---

### Étape 3 : Exécuter le programme
Deux options :
- **[3] Lancer l'execution jusqu'au BREAK** : Exécute le programme complet
- **[4] Avancer d'une seule instruction** : Exécution pas-à-pas pour debug

---

### Étape 4 : Consulter les registres
**Menu : [5] Inspecter les registres et le compteur de programme**

Affiche :
- Les 16 registres (r0 à r15) avec leurs valeurs en décimal et hexadécimal
- Le compteur de programme (PC) indiquant la prochaine instruction

```
État des registres (r0 à r15) :
  Registre r0  :   1 (0x01)
  Registre r1  :   2 (0x02)
  Registre r2  :   3 (0x03)
  ... (r3 à r15 à 0)
Compteur de programme (PC) : 12
```

---

### Étape 5 : Inspecter la mémoire
**Menu : [6] Inspecter une plage memoire**

Permet de consulter l'état de la mémoire à des adresses spécifiques.

**Exemple :**
```
Entrez l'adresse de debut (0-65535) : 0
Entrez l'adresse de fin (0-65535) : 20

État de la mémoire (adresses 0 à 20) :
  Adresse mémoire     0 :   1 (0x01)    ← Opcode LOAD_CONST
  Adresse mémoire     1 :   0 (0x00)    ← Registre destination (r0)
  Adresse mémoire     2 :   1 (0x01)    ← Valeur constante
  Adresse mémoire     3 :   1 (0x01)    ← Opcode LOAD_CONST
  Adresse mémoire     4 :   1 (0x00)    ← Registre destination (r1)
  ...
```

---

## 📊 Encodage des instructions en mémoire

Chaque instruction est encodée selon un format précis :

### LOAD_CONST (Opcode 1) - Charger une constante
```
Byte 0 : Opcode (1)
Byte 1 : Numéro du registre destination (0-15)
Byte 2 : Valeur constante (0-255)
Total : 3 octets
```

**Exemple : `load r0,1`**
```
Mémoire[0] = 1    (Opcode LOAD_CONST)
Mémoire[1] = 0    (Registre r0)
Mémoire[2] = 1    (Valeur 1)
```

### LOAD_MEM (Opcode 2) - Charger depuis la mémoire
```
Byte 0 : Opcode (2)
Byte 1 : Registre destination
Byte 2-3 : Adresse mémoire (16 bits)
Total : 4 octets
```

### STORE (Opcode 3) - Stocker en mémoire
```
Byte 0 : Opcode (3)
Byte 1 : Registre source
Byte 2-3 : Adresse mémoire
Total : 4 octets
```

### Instructions arithmétiques (Opcodes 4-10)
```
Byte 0 : Opcode (ADD=4, SUB=5, MUL=6, DIV=7, AND=8, OR=9, XOR=10)
Byte 1 : Registre destination
Byte 2 : Registre source 1
Byte 3 : Registre source 2
Total : 4 octets
```

### JUMP (Opcode 11)
```
Byte 0 : Opcode (11)
Byte 1-2 : Adresse cible
Total : 3 octets
```

### BEQ / BNE (Opcodes 12-13)
```
Byte 0 : Opcode (BEQ=12, BNE=13)
Byte 1 : Registre source 1
Byte 2 : Registre source 2
Byte 3-4 : Adresse cible
Total : 5 octets
```

---

## 💡 Exemple pratique : Additionner deux nombres

### Programme assembleur
```
load r0, 5       # Charger 5 dans r0
load r1, 3       # Charger 3 dans r1
add r2, r0, r1   # Additionner r0 + r1, résultat dans r2
store r2, 100    # Stocker r2 à l'adresse 100
break            # Arrêter
```

### Étapes
1. Saisissez le programme (option 1)
2. Assemblez (option 2)
3. Exécutez (option 3)
4. Consultez les registres (option 5) → r2 = 8
5. Inspectez la mémoire (option 6) → adresse 100 = 8

---

## 🐛 Débogage pas-à-pas

Pour tester votre programme étape par étape :
1. Assemblez votre programme (option 2)
2. Utilisez option 4 pour avancer ligne par ligne
3. Après chaque étape, inspectez les registres (option 5) et la mémoire (option 6)
4. Vérifiez que chaque instruction produit le résultat attendu

---

## 📝 Codes d'opération (Opcodes)

| Valeur | Mnémonique    | Description                     |
|--------|---------------|---------------------------------|
| 0      | BREAK         | Arrête l'exécution              |
| 1      | LOAD_CONST    | Charger une constante           |
| 2      | LOAD_MEM      | Charger depuis la mémoire       |
| 3      | STORE         | Stocker en mémoire              |
| 4      | ADD           | Addition                        |
| 5      | SUB           | Soustraction                    |
| 6      | MUL           | Multiplication                  |
| 7      | DIV           | Division (quotient + reste)     |
| 8      | AND           | ET binaire                      |
| 9      | OR            | OU binaire                      |
| 10     | XOR           | OU exclusif                     |
| 11     | JUMP          | Saut inconditionnel             |
| 12     | BEQ           | Saut si égal                    |
| 13     | BNE           | Saut si non égal                |

---

## ✅ Résumé

Le simulateur offre une **interface interactive complète** pour :
- ✅ Écrire et tester des programmes assembleur
- ✅ Voir comment le code est encodé en mémoire
- ✅ Consulter l'état des registres après chaque instruction
- ✅ Inspecter la mémoire à tout moment
- ✅ Déboguer pas-à-pas

**Utilisez ces outils pour apprendre le fonctionnement interne d'un processeur !**
