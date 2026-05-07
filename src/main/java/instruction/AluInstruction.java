package instruction;

import core.ALU;
import core.Memory;
import core.RegisterFile;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;

/**
 * Instruction ALU — gère toutes les opérations arithmétiques et logiques.
 *
 * Opérations gérées :
 * ADD r2, r0, r1  →  r2 = r0 + r1
 * SUB r2, r0, r1  →  r2 = r0 - r1
 * MUL r2, r3, r0, r1  →  r2:r3 = r0 × r1
 * DIV r2, r3, r0, r1  →  r2 = r0 / r1, r3 = r0 % r1
 * AND r2, r0, r1  →  r2 = r0 AND r1
 * OR  r2, r0, r1  →  r2 = r0 OR  r1
 * XOR r2, r0, r1  →  r2 = r0 XOR r1
 */
public class AluInstruction extends Instruction {

    /** Registre destination principal. */
    private final int regDest;

    /** Registre destination secondaire (MUL et DIV uniquement). */
    private final int regDest2;

    /** Registre source A. */
    private final int regA;

    /** Registre source B. */
    private final int regB;

    /**
     * Construit une instruction ALU.
     *
     * @param opcode   l'opération à effectuer (ADD, SUB, MUL, DIV, AND, OR, XOR)
     * @param regDest  registre destination principal
     * @param regDest2 registre destination secondaire (MUL et DIV uniquement, -1 sinon)
     * @param regA     registre source A
     * @param regB     registre source B
     */
    public AluInstruction(Opcode opcode, int regDest, int regDest2,
                          int regA, int regB) {
        super(opcode);
        this.regDest  = regDest;
        this.regDest2 = regDest2;
        this.regA     = regA;
        this.regB     = regB;
    }

    /**
     * Exécute l'instruction ALU.
     *
     * Pour ADD, SUB, AND, OR, XOR :
     * 1. Lit les valeurs de regA et regB
     * 2. Effectue le calcul via l'ALU
     * 3. Écrit le résultat dans regDest
     *
     * Pour MUL et DIV :
     * 1. Lit les valeurs de regA et regB
     * 2. Effectue le calcul via l'ALU
     * 3. Écrit le résultat haut dans regDest
     * 4. Écrit le résultat bas dans regDest2
     *
     * @param registerFile le banc de registres du CPU
     * @param memory       la mémoire (non utilisée ici)
     * @param alu          l'ALU pour effectuer les calculs
     * @throws RegisterOutOfBoundsException si un registre est invalide
     */
    @Override
    public void execute(RegisterFile registerFile, Memory memory, ALU alu)
            throws RegisterOutOfBoundsException, MemoryOutOfBoundsException {

        // 1. Lit les valeurs des registres sources
        byte a = registerFile.get(regA);
        byte b = registerFile.get(regB);

        // 2. Effectue le calcul selon l'opcode
        switch (getOpcode()) {

            case ADD:
                // Addition simple → 1 résultat
                registerFile.set(regDest, alu.add(a, b));
                break;

            case SUB:
                // Soustraction simple → 1 résultat
                registerFile.set(regDest, alu.sub(a, b));
                break;

            case MUL:
                // Multiplication → 2 résultats (haut et bas)
                byte[] mulResult = alu.mul(a, b);
                // mulResult[0] = octet de poids fort
                // mulResult[1] = octet de poids faible
                registerFile.set(regDest,  mulResult[0]);
                registerFile.set(regDest2, mulResult[1]);
                break;

            case DIV:
                // Division → 2 résultats (quotient et reste)
                byte[] divResult = alu.div(a, b);
                // divResult[0] = quotient
                // divResult[1] = reste
                registerFile.set(regDest,  divResult[0]);
                registerFile.set(regDest2, divResult[1]);
                break;

            case AND:
                // ET binaire → 1 résultat
                registerFile.set(regDest, alu.and(a, b));
                break;

            case OR:
                // OU binaire → 1 résultat
                registerFile.set(regDest, alu.or(a, b));
                break;

            case XOR:
                // OU exclusif → 1 résultat
                registerFile.set(regDest, alu.xor(a, b));
                break;

            default:
                throw new IllegalStateException(
                    "Opcode ALU invalide : " + getOpcode()
                );
        }
    }
}