package instruction;

import core.ALU;
import core.Memory;
import core.RegisterFile;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;

/**
 * Instruction LOAD_MEM — charge en registre une valeur lue en mémoire.
 *
 * Exemple assembleur : LOAD_MEM r2, @100
 * Ce que ça fait     : r2 = mémoire[100]
 *
 * Octets en mémoire  : [02][02][00][64]
 *                        ↑   ↑   ↑   ↑
 *                     opcode r2  addrH addrL
 *                                (100 = 0x0064)
 */
public class LoadMemInstruction extends Instruction {

    /** Numéro du registre destination (0 à 15). */
    private final int registerIndex;

    /** Adresse mémoire source sur 16 bits. */
    private final int address;

    /**
     * Construit une instruction LOAD_MEM.
     *
     * @param registerIndex numéro du registre destination (0 à 15)
     * @param address       adresse mémoire source (0 à 65535)
     */
    public LoadMemInstruction(int registerIndex, int address) {
        super(Opcode.LOAD_MEM);
        this.registerIndex = registerIndex;
        this.address = address;
    }

    /**
     * Exécute l'instruction LOAD_MEM.
     * Lit la valeur à l'adresse mémoire et l'écrit dans le registre.
     *
     * Boucle de lecture :
     * 1. Va chercher l'octet à l'adresse mémoire donnée
     * 2. Écrit cet octet dans le registre destination
     *
     * @param registerFile le banc de registres du CPU
     * @param memory       la mémoire principale du CPU
     * @param alu          l'ALU (non utilisée ici)
     * @throws MemoryOutOfBoundsException   si l'adresse est invalide
     * @throws RegisterOutOfBoundsException si le registre est invalide
     */
    @Override
    public void execute(RegisterFile registerFile, Memory memory, ALU alu)
            throws MemoryOutOfBoundsException, RegisterOutOfBoundsException {
        // 1. Lit l'octet à l'adresse mémoire donnée
        byte value = memory.readByte(address);
        // 2. Écrit cet octet dans le registre destination
        registerFile.set(registerIndex, value);
    }
}
