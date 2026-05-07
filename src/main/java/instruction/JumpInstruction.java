package instruction;

import core.ALU;
import core.Memory;
import core.ProgramCounter;
import core.RegisterFile;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;

/**
 * Instruction JUMP — saut inconditionnel à une adresse mémoire.
 *
 * Exemple assembleur : JUMP @50
 * Ce que ça fait     : PC = 50
 *                      le CPU continue depuis l'adresse 50
 *
 * Octets en mémoire  : [11][00][32]
 *                        ↑   ↑   ↑
 *                     opcode addrH addrL
 *                            (50 = 0x0032)
 */
public class JumpInstruction extends Instruction {

    /** Adresse de destination du saut sur 16 bits. */
    private final int address;

    /** Compteur de programme à modifier pour effectuer le saut. */
    private final ProgramCounter pc;

    /**
     * Construit une instruction JUMP.
     *
     * @param address adresse de destination du saut (0 à 65535)
     * @param pc      le compteur de programme du CPU
     */
    public JumpInstruction(int address, ProgramCounter pc) {
        super(Opcode.JUMP);
        this.address = address;
        this.pc      = pc;
    }

    /**
     * Exécute l'instruction JUMP.
     * Modifie directement le compteur de programme
     * pour sauter à l'adresse destination.
     *
     * @param registerFile le banc de registres (non utilisé)
     * @param memory       la mémoire (non utilisée)
     * @param alu          l'ALU (non utilisée)
     */
    @Override
    public void execute(RegisterFile registerFile, Memory memory, ALU alu)
            throws RegisterOutOfBoundsException, MemoryOutOfBoundsException {
        // Saute directement à l'adresse destination
        // Le prochain FETCH lira depuis cette adresse
        pc.set(address);
    }
}
