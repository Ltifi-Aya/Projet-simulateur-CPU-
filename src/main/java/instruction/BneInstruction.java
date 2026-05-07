package instruction;

import core.ALU;
import core.Memory;
import core.ProgramCounter;
import core.RegisterFile;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;

/**
 * Instruction BNE — saut conditionnel si deux registres sont différents.
 *
 * Exemple assembleur : BNE r0, r1, @50
 * Ce que ça fait     : SI r0 != r1 ALORS PC = 50
 *                      SINON continue normalement
 *
 * Octets en mémoire  : [13][00][01][00][32]
 *                        ↑   ↑   ↑   ↑   ↑
 *                     opcode r0  r1 addrH addrL
 */
public class BneInstruction extends Instruction {

    /** Numéro du premier registre à comparer. */
    private final int regA;

    /** Numéro du deuxième registre à comparer. */
    private final int regB;

    /** Adresse de destination si la condition est vraie. */
    private final int address;

    /** Compteur de programme à modifier si la condition est vraie. */
    private final ProgramCounter pc;

    /**
     * Construit une instruction BNE.
     *
     * @param regA    numéro du premier registre (0 à 15)
     * @param regB    numéro du deuxième registre (0 à 15)
     * @param address adresse de destination si différents (0 à 65535)
     * @param pc      le compteur de programme du CPU
     */
    public BneInstruction(int regA, int regB, int address, ProgramCounter pc) {
        super(Opcode.BNE);
        this.regA    = regA;
        this.regB    = regB;
        this.address = address;
        this.pc      = pc;
    }

    /**
     * Exécute l'instruction BNE.
     *
     * Étapes :
     * 1. Lit les valeurs de regA et regB
     * 2. Compare les deux valeurs
     * 3. Si différentes → modifie le PC pour sauter à l'adresse
     *    Si égales      → ne fait rien, le CPU continue normalement
     *
     * IMPORTANT : les paramètres sont toujours lus AVANT
     * l'évaluation de la condition — le PC est donc toujours
     * avancé au-delà de l'instruction complète.
     *
     * @param registerFile le banc de registres du CPU
     * @param memory       la mémoire (non utilisée)
     * @param alu          l'ALU (non utilisée)
     * @throws RegisterOutOfBoundsException si un registre est invalide
     */
    @Override
    public void execute(RegisterFile registerFile, Memory memory, ALU alu)
            throws RegisterOutOfBoundsException, MemoryOutOfBoundsException {

        // 1. Lit les valeurs des deux registres à comparer
        byte valeurA = registerFile.get(regA);
        byte valeurB = registerFile.get(regB);

        // 2. Compare les deux valeurs
        // Si différentes → saute à l'adresse destination
        if (valeurA != valeurB) {
            pc.set(address);
        }
        // Sinon → ne fait rien, le CPU continue normalement
    }
}