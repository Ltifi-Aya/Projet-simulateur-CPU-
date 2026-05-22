# 🚀 Test rapide du Simulateur CPU

Ce document montre comment tester rapidement le simulateur avec des exemples.

---

## Test 1 : Charger une constante
**Commandes à saisir :**
```
1                  # Menu [1] - Saisir un programme
load r0, 42        # Charger 42 dans r0
break              # Arrêter
FIN                # Fin de la saisie
2                  # Menu [2] - Assembler
3                  # Menu [3] - Exécuter
5                  # Menu [5] - Voir les registres
10                 # Quitter
```

**Résultat attendu :**
```
Registre r0  :  42 (0x2A)
Registre r1-r15 : 0
```

---

## Test 2 : Addition
**Commandes à saisir :**
```
1                  # Menu [1]
load r0, 10        # r0 = 10
load r1, 5         # r1 = 5
add r2, r0, r1     # r2 = r0 + r1 = 15
break
FIN
2                  # Assembler
3                  # Exécuter
5                  # Voir registres
10
```

**Résultat attendu :**
```
Registre r0  :  10 (0x0A)
Registre r1  :   5 (0x05)
Registre r2  :  15 (0x0F)
```

---

## Test 3 : Stocker et charger depuis la mémoire
**Commandes à saisir :**
```
1
load r0, 99        # r0 = 99
store r0, 100      # Stocker r0 à l'adresse 100
load r1, @100      # Charger depuis mémoire[100] dans r1
break
FIN
2
3
5                  # Vérifier que r0=99 et r1=99
6                  # Inspecter la mémoire
100                # Adresse de début
100                # Adresse de fin
10
```

**Résultat attendu :**
```
Registre r0  :  99 (0x63)
Registre r1  :  99 (0x63)

Mémoire[100] :  99 (0x63)
```

---

## Test 4 : Multiplication
**Commandes à saisir :**
```
1
load r0, 7         # r0 = 7
load r1, 8         # r1 = 8
mul r2, r0, r1     # r2 = 56
break
FIN
2
3
5
10
```

**Résultat attendu :**
```
Registre r0  :   7 (0x07)
Registre r1  :   8 (0x08)
Registre r2  :  56 (0x38)
```

---

## Test 5 : Saut conditionnel (BEQ)
**Commandes à saisir :**
```
1
load r0, 7         # r0 = 7
load r1, 7         # r1 = 7
beq r0, r1, 20     # Sauter si r0 == r1 (oui, donc saut)
load r2, 999       # Cette ligne ne s'exécute pas
load r3, 888       # Cette ligne ne s'exécute pas
break              # Adresse 20 - Point de saut
FIN
2
3
5
10
```

**Résultat attendu :**
```
Registre r0  :   7 (0x07)
Registre r1  :   7 (0x07)
Registre r2  :   0 (0x00)  ← Ne s'exécute pas
Registre r3  :   0 (0x00)  ← Ne s'exécute pas
```

---

## Test 6 : Pas-à-pas (Debug)
**Commandes à saisir :**
```
1
load r0, 100
load r1, 50
add r2, r0, r1
break
FIN
2
4                  # Pas-à-pas (au lieu de [3])
4                  # Prochaine étape
4                  # Prochaine étape
4                  # Prochaine étape
5                  # Vérifier les registres
10
```

**À chaque étape [4], vous pouvez :**
- Appuyer sur [5] pour voir l'état des registres
- Appuyer sur [6] pour inspecter la mémoire
- Puis revenir au menu avec [4] pour continuer

---

## Test 7 : Vérifier la mémoire après assemblage
**Commandes à saisir :**
```
1
load r0, 1
break
FIN
2
6                  # Inspecter la mémoire AVANT exécution
0                  # Adresse début
10                 # Adresse fin
3                  # Exécuter
6                  # Inspecter la mémoire APRÈS exécution
0
10
10
```

**Résultat attendu :**
```
AVANT exécution (mémoire contient le code assemblé) :
  Adresse mémoire     0 :   1 (0x01)  ← Opcode LOAD_CONST
  Adresse mémoire     1 :   0 (0x00)  ← Registre r0
  Adresse mémoire     2 :   1 (0x01)  ← Valeur 1
  Adresse mémoire     3 :   0 (0x00)  ← Opcode BREAK

APRÈS exécution (mêmes données) :
  (Idem)
```

---

## 💡 Conseils pour tester

### ✅ Tester votre propre programme
1. Écrivez votre programme en assembleur
2. Choisissez option [1] et saisissez les instructions
3. Terminez avec "FIN"
4. Choisissez [2] pour assembler
5. Choisissez [4] pour pas-à-pas (plus facile pour debug)
6. Après chaque étape, inspectez les registres avec [5]
7. Inspectez la mémoire avec [6] si nécessaire

### 🐛 Déboguer un problème
- Utilisez pas-à-pas [4] pour voir chaque instruction
- Inspectez [5] et [6] après chaque étape
- Vérifiez que les adresses en mémoire correspondent aux valeurs attendues
- Comparez avec les opcodes (voir USAGE_GUIDE.md)

### 📝 Prendre des notes
- Notez les adresses mémoire où vos données sont stockées
- Comparez avec les addresses attendues
- Utilisez l'option [7] pour revoir votre code assembleur

---

## 🎯 Prochaines étapes

1. **Testez les 7 tests ci-dessus** pour comprendre le flux
2. **Lisez EXAMPLES.md** pour voir 15 autres exemples
3. **Lisez USAGE_GUIDE.md** pour comprendre les encodages
4. **Écrivez vos propres programmes** et testez-les

---

## ⚠️ Erreurs courantes

| Erreur | Cause | Solution |
|--------|-------|----------|
| "Assemblage reussi" mais rien ne s'exécute | Manque un `break` | Ajoutez `break` à la fin |
| "Aucune donnee dans cette plage" | Adresses vides | Inspectez les adresses correctes |
| Programme arrête mais registres vides | Instructions mal écrites | Vérifiez la syntaxe (ex: `load r0, 5`) |
| Valeur trop grande (>255) | Les registres sont 8 bits | Utilisez des valeurs 0-255 |

---

**Amusez-vous à explorer le simulateur ! 🚀**
