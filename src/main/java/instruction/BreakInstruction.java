package instruction;

import core.ALU;
import core.CPU;
import core.Memory;
import core.RegisterFile;

/**
 * Instruction BREAK — arrête l'exécution du CPU.
 * C'est la seule instruction qui agit directement
 * sur l'état du CPU plutôt que sur les données.
 */
public class BreakInstruction extends Instruction {

    /**
     * Construit une instruction BREAK.
     */
    public BreakInstruction() {
        super(Opcode.BREAK);
    }

    /**
     * Exécute l'instruction BREAK.
     * Met le CPU en état d'arrêt via stop().
     *
     * @param registerFile le banc de registres (non utilisé)
     * @param memory       la mémoire (non utilisée)
     * @param alu          l'ALU (non utilisée)
     */
    @Override
    public void execute(RegisterFile registerFile, Memory memory, ALU alu) {
        // BREAK n'a pas besoin des registres ni de la mémoire
        // Le CPU sera arrêté depuis la boucle principale
    }
}