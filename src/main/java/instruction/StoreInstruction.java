package instruction;

import core.ALU;
import core.Memory;
import core.RegisterFile;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;

/**
 * Instruction STORE — sauvegarde la valeur d'un registre en mémoire.
 *
 * Exemple assembleur : STORE r0, @101
 * Ce que ça fait     : mémoire[101] = r0
 *
 * Octets en mémoire  : [03][00][00][65]
 *                        ↑   ↑   ↑   ↑
 *                     opcode r0 addrH addrL
 *                                (101 = 0x0065)
 */
public class StoreInstruction extends Instruction {

    /** Numéro du registre source (0 à 15). */
    private final int registerIndex;

    /** Adresse mémoire destination sur 16 bits. */
    private final int address;

    /**
     * Construit une instruction STORE.
     *
     * @param registerIndex numéro du registre source (0 à 15)
     * @param address       adresse mémoire destination (0 à 65535)
     */
    public StoreInstruction(int registerIndex, int address) {
        super(Opcode.STORE);
        this.registerIndex = registerIndex;
        this.address = address;
    }

    /**
     * Exécute l'instruction STORE.
     * Lit la valeur du registre source et l'écrit en mémoire.
     *
     * Étapes d'exécution :
     * 1. Lit la valeur dans le registre source
     * 2. Écrit cette valeur à l'adresse mémoire destination
     *
     * @param registerFile le banc de registres du CPU
     * @param memory       la mémoire principale du CPU
     * @param alu          l'ALU (non utilisée ici)
     * @throws RegisterOutOfBoundsException si le registre est invalide
     * @throws MemoryOutOfBoundsException   si l'adresse est invalide
     */
    @Override
    public void execute(RegisterFile registerFile, Memory memory, ALU alu)
            throws RegisterOutOfBoundsException, MemoryOutOfBoundsException {
        // 1. Lit la valeur dans le registre source
        byte value = registerFile.get(registerIndex);
        // 2. Écrit cette valeur à l'adresse mémoire destination
        memory.writeByte(address, value);
    }
}