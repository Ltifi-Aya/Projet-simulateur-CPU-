package instruction;

import core.ALU;
import core.Memory;
import core.RegisterFile;
import exception.RegisterOutOfBoundsException;
import exception.MemoryOutOfBoundsException;

/**
 * Instruction LOAD_CONST — charge une valeur constante dans un registre.
 *
 * Exemple assembleur : LOAD_CONST r0, 5
 * Ce que ça fait     : r0 = 5
 *
 * Octets en mémoire  : [01][00][05]
 *                       ↑    ↑   ↑
 *                    opcode  r0  5
 */
public class LoadConstInstruction extends Instruction {

    /** Numéro du registre destination (0 à 15). */
    private final int registerIndex;

    /** Valeur constante à charger dans le registre. */
    private final byte value;

    /**
     * Construit une instruction LOAD_CONST.
     *
     * @param registerIndex numéro du registre destination (0 à 15)
     * @param value         valeur constante à charger
     */
    public LoadConstInstruction(int registerIndex, byte value) {
        super(Opcode.LOAD_CONST);
        // On mémorise le registre destination et la valeur
        this.registerIndex = registerIndex;
        this.value = value;
    }

    /**
     * Exécute l'instruction LOAD_CONST.
     * Écrit la valeur constante dans le registre destination.
     *
     * @param registerFile le banc de registres du CPU
     * @param memory       la mémoire (non utilisée ici)
     * @param alu          l'ALU (non utilisée ici)
     * @throws RegisterOutOfBoundsException si le registre est invalide
     */
    @Override
    public void execute(RegisterFile registerFile, Memory memory, ALU alu)
            throws RegisterOutOfBoundsException, MemoryOutOfBoundsException {
        // Écrit la valeur dans le registre destination
        registerFile.set(registerIndex, value);
    }
}