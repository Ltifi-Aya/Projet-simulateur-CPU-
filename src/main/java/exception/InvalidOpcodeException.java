package exception;

/**
 * Exception levée lorsqu'un opcode inconnu ou invalide est rencontré
 * lors de l'exécution d'une instruction par le processeur.
 *
 * <p>Chaque instruction du CPU possède un code opération (opcode) bien défini.
 * Si le processeur lit un octet en mémoire qui ne correspond à aucun opcode
 * connu, cette exception est levée pour signaler l'erreur.</p>
 *
 * @author Personne 1
 * @version 1.0
 */
public class InvalidOpcodeException extends Exception {

    /** L'opcode invalide rencontré. */
    private final int opcode;

    /**
     * Construit une exception avec l'opcode invalide spécifié.
     *
     * @param opcode la valeur de l'opcode non reconnu
     */
    public InvalidOpcodeException(int opcode) {
        super("Opcode invalide ou inconnu : " + opcode
                + " (0x" + Integer.toHexString(opcode).toUpperCase() + ")");
        this.opcode = opcode;
    }

    /**
     * Retourne la valeur de l'opcode invalide qui a déclenché cette exception.
     *
     * @return l'opcode invalide (valeur numérique)
     */
    public int getOpcode() {
        return opcode;
    }
}