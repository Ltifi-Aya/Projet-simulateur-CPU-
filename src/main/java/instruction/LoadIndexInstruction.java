package instruction;

import core.ALU;
import core.Memory;
import core.RegisterFile;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;

/**
 * Instruction LOAD_INDEX — charge en registre une valeur lue à une adresse indexée.
 *
 * Exemple assembleur : LOAD r0, @100, r1
 * Ce que ça fait     : r0 = mémoire[100 + r1]
 *
 * Octets en mémoire  : [14][00][00][64][01]
 *                        ↑   ↑   ↑   ↑   ↑
 *                     opcode r0  addrH addrL rIndex
 *                                (100 = 0x0064)
 */
public class LoadIndexInstruction extends Instruction {

    /** Numéro du registre destination (0 à 15). */
    private final int registerIndex;

    /** Adresse mémoire de base sur 16 bits. */
    private final int baseAddress;

    /** Numéro du registre contenant l'offset à ajouter. */
    private final int indexRegister;

    /**
     * Construit une instruction LOAD_INDEX.
     *
     * @param registerIndex  numéro du registre destination (0 à 15)
     * @param baseAddress    adresse mémoire de base (0 à 65535)
     * @param indexRegister  numéro du registre contenant l'offset (0 à 15)
     */
    public LoadIndexInstruction(int registerIndex, int baseAddress, int indexRegister) {
        super(Opcode.LOAD_INDEX);
        this.registerIndex = registerIndex;
        this.baseAddress = baseAddress;
        this.indexRegister = indexRegister;
    }

    /**
     * Exécute l'instruction LOAD_INDEX.
     * Lit la valeur à l'adresse (baseAddress + indexRegisterValue) et l'écrit dans le registre.
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
        
        // 3. Lit l'octet à cette adresse
        byte value = memory.readByte(actualAddress);
        
        // 4. Écrit dans le registre destination
        registerFile.set(registerIndex, value);
    }
}
