# Exemples de Programmes pour le Simulateur CPU

## ✅ Exemple 1 : Charger une constante
**Objectif :** Charger la valeur 42 dans le registre r0

```
load r0, 42
break
```

**Résultat attendu :**
- Registre r0 = 42 (0x2A)
- Mémoire[0] = 1 (opcode LOAD_CONST)
- Mémoire[1] = 0 (registre r0)
- Mémoire[2] = 42 (valeur)

---

## ✅ Exemple 2 : Addition simple
**Objectif :** Calculer 5 + 3

```
load r0, 5
load r1, 3
add r2, r0, r1
break
```

**Résultat attendu :**
- Registre r0 = 5
- Registre r1 = 3
- Registre r2 = 8
- PC = 16 (après exécution)

---

## ✅ Exemple 3 : Multiplication
**Objectif :** Calculer 4 * 6

```
load r0, 4
load r1, 6
mul r2, r0, r1
break
```

**Résultat attendu :**
- Registre r0 = 4
- Registre r1 = 6
- Registre r2 = 24

---

## ✅ Exemple 4 : Soustraction
**Objectif :** Calculer 10 - 3

```
load r0, 10
load r1, 3
sub r2, r0, r1
break
```

**Résultat attendu :**
- Registre r0 = 10
- Registre r1 = 3
- Registre r2 = 7

---

## ✅ Exemple 5 : Opérations binaires (AND)
**Objectif :** Calculer 15 AND 7 (0x0F AND 0x07)

```
load r0, 15
load r1, 7
and r2, r0, r1
break
```

**Résultat attendu :**
- Registre r0 = 15 (0x0F)
- Registre r1 = 7 (0x07)
- Registre r2 = 7 (0x07)  [car 1111 AND 0111 = 0111]

---

## ✅ Exemple 6 : Opérations binaires (OR)
**Objectif :** Calculer 8 OR 4 (0x08 OR 0x04)

```
load r0, 8
load r1, 4
or r2, r0, r1
break
```

**Résultat attendu :**
- Registre r0 = 8 (0x08)
- Registre r1 = 4 (0x04)
- Registre r2 = 12 (0x0C)  [car 1000 OR 0100 = 1100]

---

## ✅ Exemple 7 : Opérations binaires (XOR)
**Objectif :** Calculer 12 XOR 5

```
load r0, 12
load r1, 5
xor r2, r0, r1
break
```

**Résultat attendu :**
- Registre r0 = 12 (0x0C)
- Registre r1 = 5 (0x05)
- Registre r2 = 9 (0x09)  [car 1100 XOR 0101 = 1001]

---

## ✅ Exemple 8 : Stocker une valeur en mémoire
**Objectif :** Charger 99 dans r0, puis le stocker à l'adresse 50

```
load r0, 99
store r0, 50
break
```

**Résultat attendu :**
- Registre r0 = 99
- Mémoire[50] = 99
- Inspectez la mémoire (adresses 45-55) pour confirmer

---

## ✅ Exemple 9 : Charger depuis la mémoire
**Objectif :** Charger une valeur stockée en mémoire

```
load r0, 42
store r0, 60
load r1, @60
break
```

**Résultat attendu :**
- Registre r0 = 42
- Registre r1 = 42 (chargé depuis mémoire[60])
- Mémoire[60] = 42

---

## ✅ Exemple 10 : Saut inconditionnel (JUMP)
**Objectif :** Charger des valeurs et utiliser un saut

```
load r0, 1
jump 9
load r1, 2
load r2, 3
break
```

**Résultat attendu :**
- Registre r0 = 1
- Registre r1 = 0 (jamais exécuté)
- Registre r2 = 0 (jamais exécuté)
- Le PC saute de l'adresse 6 (après load r0,1) à l'adresse 9 (load r2,3)

---

## ✅ Exemple 11 : Saut conditionnel (BEQ - Branch if Equal)
**Objectif :** Sauter si deux registres sont égaux

```
load r0, 5
load r1, 5
beq r0, r1, 15
load r2, 99
load r3, 100
break
```

**Résultat attendu :**
- Registre r0 = 5
- Registre r1 = 5
- Registre r2 = 0 (jamais exécuté, car saut effectué)
- Registre r3 = 0 (jamais exécuté)
- Le PC saute à l'adresse 15 (break) car r0 == r1

---

## ✅ Exemple 12 : Saut conditionnel (BNE - Branch if Not Equal)
**Objectif :** Sauter si deux registres sont différents

```
load r0, 7
load r1, 3
bne r0, r1, 15
load r2, 100
load r3, 200
break
```

**Résultat attendu :**
- Registre r0 = 7
- Registre r1 = 3
- Registre r2 = 0 (jamais exécuté, car saut effectué)
- Registre r3 = 0 (jamais exécuté)
- Le PC saute à l'adresse 15 (break) car r0 != r1

---

## ✅ Exemple 13 : Boucle simple (compter jusqu'à 3)
**Objectif :** Utiliser une boucle pour compter

```
load r0, 0
load r1, 1
add r0, r0, r1
bne r0, r1, 9
break
```

**Résultat attendu :**
- Boucle qui s'exécute et accumule des valeurs
- Après exécution complète, PC pointe vers l'instruction BREAK

---

## ✅ Exemple 14 : Division entière
**Objectif :** Calculer 20 / 3

```
load r0, 20
load r1, 3
div r2, r0, r1
break
```

**Résultat attendu :**
- Registre r0 = 20
- Registre r1 = 3
- Registre r2 = 6 (quotient)
- Registre r3 = 2 (reste, stocké automatiquement)

---

## ✅ Exemple 15 : Calcul complexe
**Objectif :** (10 + 5) * 2

```
load r0, 10
load r1, 5
add r2, r0, r1
load r3, 2
mul r4, r2, r3
break
```

**Résultat attendu :**
- Registre r0 = 10
- Registre r1 = 5
- Registre r2 = 15 (10 + 5)
- Registre r3 = 2
- Registre r4 = 30 (15 * 2)

---

## 🎯 Comment utiliser ces exemples

1. **Démarrez le simulateur**
2. **Choisir option [1]** : Saisir un programme
3. **Copier-coller** un des exemples ci-dessus (sans les commentaires #)
4. **Taper "FIN"** pour terminer la saisie
5. **Choisir option [2]** : Assembler
6. **Choisir option [3]** : Exécuter (ou option [4] pour pas-à-pas)
7. **Choisir option [5]** : Inspecter les registres
8. **Choisir option [6]** : Inspecter la mémoire (optionnel)

---

## 💡 Astuces

- **Débogage pas-à-pas** : Utilisez l'option [4] pour avancer instruction par instruction
- **Vérifier la mémoire** : Après chaque étape, inspectez la mémoire avec l'option [6]
- **Vérifier les registres** : Utilisez l'option [5] pour voir l'état des registres
- **Réinitialiser** : Utilisez l'option [9] pour recommencer avec un état vierge

---

## 📊 Tableau de synthèse des instructions

| Instruction | Format | Exemple | Résultat |
|-------------|--------|---------|----------|
| LOAD_CONST | load r<N>, <valeur> | load r0, 42 | r0 = 42 |
| LOAD_MEM | load r<N>, @<addr> | load r0, @100 | r0 = mémoire[100] |
| STORE | store r<N>, <addr> | store r0, 100 | mémoire[100] = r0 |
| ADD | add r<d>, r<s1>, r<s2> | add r2, r0, r1 | r2 = r0 + r1 |
| SUB | sub r<d>, r<s1>, r<s2> | sub r2, r0, r1 | r2 = r0 - r1 |
| MUL | mul r<d>, r<s1>, r<s2> | mul r2, r0, r1 | r2 = r0 * r1 |
| DIV | div r<d>, r<s1>, r<s2> | div r2, r0, r1 | r2 = r0 / r1, r3 = reste |
| AND | and r<d>, r<s1>, r<s2> | and r2, r0, r1 | r2 = r0 & r1 |
| OR | or r<d>, r<s1>, r<s2> | or r2, r0, r1 | r2 = r0 \| r1 |
| XOR | xor r<d>, r<s1>, r<s2> | xor r2, r0, r1 | r2 = r0 ^ r1 |
| JUMP | jump <addr> | jump 20 | PC = 20 |
| BEQ | beq r<1>, r<2>, <addr> | beq r0, r1, 30 | if r0 == r1: PC = 30 |
| BNE | bne r<1>, r<2>, <addr> | bne r0, r1, 30 | if r0 != r1: PC = 30 |
| BREAK | break | break | Fin du programme |

---

Bon apprentissage ! 🚀
