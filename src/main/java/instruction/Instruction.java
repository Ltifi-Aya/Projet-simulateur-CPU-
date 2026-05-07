package instruction;

import core.Memory;
import core.RegisterFile;
import core.ALU;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;

/**
 * Classe abstraite représentant une instruction du simulateur CPU.
 *
 * <p>Chaque instruction du processeur hérite de cette classe et implémente
 * la méthode {@link #execute(RegisterFile, Memory, ALU)} qui définit le
 * comportement spécifique de l'instruction.</p>
 *
 * <p>Une instruction est composée de :</p>
 * <ul>
 *   <li>Un {@link Opcode} identifiant le type d'opération à effectuer.</li>
 *   <li>Des paramètres lus depuis la mémoire après l'opcode (variables selon l'instruction).</li>
 * </ul>
 *
 * <p>Cycle d'exécution d'une instruction :</p>
 * <ol>
 *   <li>Le CPU lit l'opcode depuis la mémoire à l'adresse du compteur de programme (PC).</li>
 *   <li>Le CPU incrémente le PC.</li>
 *   <li>L'instruction correspondante est instanciée.</li>
 *   <li>Les paramètres sont lus depuis la mémoire (le PC avance à chaque lecture).</li>
 *   <li>La méthode {@link #execute} est appelée.</li>
 * </ol>
 *
 * <p>Exemple de sous-classe concrète :</p>
 * <pre>
 * public class LoadConstInstruction extends Instruction {
 *     private final int registerIndex;
 *     private final byte value;
 *
 *     public LoadConstInstruction(int registerIndex, byte value) {
 *         super(Opcode.LOAD_CONST);
 *         this.registerIndex = registerIndex;
 *         this.value = value;
 *     }
 *
 *     {@literal @}Override
 *     public void execute(RegisterFile rf, Memory mem, ALU alu)
 *             throws RegisterOutOfBoundsException {
 *         rf.set(registerIndex, value);
 *     }
 * }
 * </pre>
 *
 * @author Personne 2
 * @version 1.0
 * @see Opcode
 * @see core.Memory
 * @see core.RegisterFile
 * @see core.ALU
 */
public abstract class Instruction {

    /** L'opcode identifiant cette instruction. */
    private final Opcode opcode;

    /**
     * Construit une instruction avec l'opcode spécifié.
     *
     * @param opcode l'opcode de cette instruction (ne doit pas être {@code null})
     * @throws IllegalArgumentException si {@code opcode} est {@code null}
     */
    protected Instruction(Opcode opcode) {
        if (opcode == null) {
            throw new IllegalArgumentException("L'opcode ne peut pas être null");
        }
        this.opcode = opcode;
    }

    /**
     * Retourne l'opcode de cette instruction.
     *
     * @return l'opcode associé à cette instruction
     */
    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * Exécute l'instruction sur le CPU simulé.
     *
     * <p>Cette méthode doit être implémentée par chaque sous-classe concrète.
     * Elle définit le comportement exact de l'instruction : lecture/écriture
     * dans les registres, accès mémoire, calculs arithmétiques ou logiques, etc.</p>
     *
     * @param registerFile le banc de registres du CPU (lecture et écriture)
     * @param memory       la mémoire principale du CPU (lecture et écriture)
     * @param alu          l'unité arithmétique et logique (pour les calculs)
     * @throws RegisterOutOfBoundsException si un accès à un registre invalide est tenté
     * @throws MemoryOutOfBoundsException   si un accès à une adresse mémoire invalide est tenté
     * @throws ArithmeticException          si une opération arithmétique impossible est tentée
     *                                      (ex : division par zéro)
     */
    public abstract void execute(RegisterFile registerFile, Memory memory, ALU alu)
            throws RegisterOutOfBoundsException, MemoryOutOfBoundsException;

    /**
     * Retourne une représentation textuelle de cette instruction.
     *
     * <p>Par défaut, affiche le nom de l'opcode. Les sous-classes peuvent
     * surcharger cette méthode pour afficher également leurs paramètres.</p>
     *
     * @return une chaîne représentant cette instruction
     */
    @Override
    public String toString() {
        return "Instruction[" + opcode.name() + "]";
    }
}