package instruction;

import core.ALU;
import core.Memory;
import core.RegisterFile;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;

/**
 * Instruction STORE_INDEX — sauvegarde un registre en mémoire à une adresse indexée.
 *
 * Exemple assembleur : STORE r0, @100, r1
 * Ce que ça fait     : mémoire[100 + r1] = r0
 *
 * Octets en mémoire  : [15][00][00][64][01]
 *                        ↑   ↑   ↑   ↑   ↑
 *                     opcode r0  addrH addrL rIndex
 *                                (100 = 0x0064)
 */
public class StoreIndexInstruction extends Instruction {

    /** Numéro du registre source (0 à 15). */
    private final int registerIndex;

    /** Adresse mémoire de base sur 16 bits. */
    private final int baseAddress;

    /** Numéro du registre contenant l'offset à ajouter. */
    private final int indexRegister;

    /**
     * Construit une instruction STORE_INDEX.
     *
     * @param registerIndex  numéro du registre source (0 à 15)
     * @param baseAddress    adresse mémoire de base (0 à 65535)
     * @param indexRegister  numéro du registre contenant l'offset (0 à 15)
     */
    public StoreIndexInstruction(int registerIndex, int baseAddress, int indexRegister) {
        super(Opcode.STORE_INDEX);
        this.registerIndex = registerIndex;
        this.baseAddress = baseAddress;
        this.indexRegister = indexRegister;
    }

    /**
     * Exécute l'instruction STORE_INDEX.
     * Écrit la valeur du registre en mémoire à l'adresse (baseAddress + indexRegisterValue).
     *
     * @param registerFile le banc de registres du CPU
     * @param memory       la mémoire principale du CPU
     * @param alu          l'ALU (non utilisée ici)
     * @throws MemoryOutOfBoundsException   si l'adresse calculée est invalide
     * @throws RegisterOutOfBoundsException si un registre est invalide
     */
    @Override
    public void execute(RegisterFile registerFile, Memory memory, ALU alu)
            throws MemoryOutOfBoundsException, RegisterOutOfBoundsException {
        // 1. Récupère la valeur du registre index (offset)
        byte indexValue = registerFile.get(indexRegister);
        // Convertit en unsigned pour éviter les valeurs négatives
        int offset = indexValue & 0xFF;
        
        // 2. Calcule l'adresse réelle = adresse de base + offset
        int actualAddress = baseAddress + offset;
        
        // 3. Récupère la valeur du registre source
        byte value = registerFile.get(registerIndex);
        
        // 4. Écrit en mémoire à cette adresse
        memory.writeByte(actualAddress, value);
    }
}
