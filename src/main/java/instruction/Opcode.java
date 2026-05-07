package instruction;

/**
 * Enumération de tous les codes opération (opcodes) supportés par le simulateur CPU.
 *
 * <p>Chaque opcode est associé à une valeur entière unique qui est stockée en mémoire
 * pour représenter une instruction. Le processeur lit cet octet depuis la mémoire,
 * l'identifie via cet enum, puis exécute l'opération correspondante.</p>
 *
 * <p>Table des opcodes :</p>
 * <pre>
 *  Valeur | Mnémonique      | Description
 *  -------+-----------------+----------------------------------------
 *    0    | BREAK           | Arrête l'exécution du programme
 *    1    | LOAD_CONST      | Charge une constante dans un registre
 *    2    | LOAD_MEM        | Charge une valeur mémoire dans un registre
 *    3    | STORE           | Sauvegarde un registre en mémoire
 *    4    | ADD             | Addition de deux registres
 *    5    | SUB             | Soustraction de deux registres
 *    6    | MUL             | Multiplication de deux registres (résultat 16 bits)
 *    7    | DIV             | Division de deux registres (quotient + reste)
 *    8    | AND             | ET binaire sur deux registres
 *    9    | OR              | OU binaire sur deux registres
 *   10    | XOR             | OU exclusif sur deux registres
 * </pre>
 *
 * @author Personne 2
 * @version 1.0
 */
public enum Opcode {

    /**
     * Arrête l'exécution du programme.
     * <p>Opcode : {@code 0}</p>
     * <p>Paramètres : aucun.</p>
     */
    BREAK(0),

    /**
     * Charge une valeur constante dans un registre.
     * <p>Opcode : {@code 1}</p>
     * <p>Paramètres : numéro du registre destination (1 octet), valeur constante (1 octet).</p>
     * <p>Exemple assembleur : {@code load r0, 5}</p>
     */
    LOAD_CONST(1),

    /**
     * Charge en registre la valeur lue à une adresse mémoire.
     * <p>Opcode : {@code 2}</p>
     * <p>Paramètres : numéro du registre destination (1 octet), adresse mémoire (2 octets).</p>
     * <p>Exemple assembleur : {@code load r2, @100}</p>
     */
    LOAD_MEM(2),

    /**
     * Sauvegarde la valeur d'un registre en mémoire.
     * <p>Opcode : {@code 3}</p>
     * <p>Paramètres : numéro du registre source (1 octet), adresse mémoire destination (2 octets).</p>
     * <p>Exemple assembleur : {@code store r0, @101}</p>
     */
    STORE(3),

    /**
     * Additionne deux registres et stocke le résultat dans un troisième.
     * <p>Opcode : {@code 4}</p>
     * <p>Paramètres : registre destination, registre A, registre B (1 octet chacun).</p>
     * <p>Exemple assembleur : {@code add r2, r0, r1} → r2 = r0 + r1</p>
     */
    ADD(4),

    /**
     * Soustrait deux registres et stocke le résultat dans un troisième.
     * <p>Opcode : {@code 5}</p>
     * <p>Paramètres : registre destination, registre A, registre B (1 octet chacun).</p>
     * <p>Exemple assembleur : {@code sub r2, r0, r1} → r2 = r0 - r1</p>
     */
    SUB(5),

    /**
     * Multiplie deux registres et stocke le résultat 16 bits dans deux registres.
     * <p>Opcode : {@code 6}</p>
     * <p>Paramètres : registre destination haut, registre destination bas,
     *    registre A, registre B (1 octet chacun).</p>
     * <p>Exemple assembleur : {@code mul r2, r3, r0, r1} → r2:r3 = r0 × r1</p>
     */
    MUL(6),

    /**
     * Divise deux registres et stocke quotient et reste dans deux registres.
     * <p>Opcode : {@code 7}</p>
     * <p>Paramètres : registre quotient, registre reste, registre A, registre B (1 octet chacun).</p>
     * <p>Exemple assembleur : {@code div r2, r3, r0, r1} → r2 = r0 / r1, r3 = r0 % r1</p>
     */
    DIV(7),

    /**
     * Effectue un ET binaire sur deux registres.
     * <p>Opcode : {@code 8}</p>
     * <p>Paramètres : registre destination, registre A, registre B (1 octet chacun).</p>
     * <p>Exemple assembleur : {@code and r2, r0, r1} → r2 = r0 AND r1</p>
     */
    AND(8),

    /**
     * Effectue un OU binaire sur deux registres.
     * <p>Opcode : {@code 9}</p>
     * <p>Paramètres : registre destination, registre A, registre B (1 octet chacun).</p>
     * <p>Exemple assembleur : {@code or r2, r0, r1} → r2 = r0 OR r1</p>
     */
    OR(9),

    /**
     * Effectue un OU exclusif sur deux registres.
     * <p>Opcode : {@code 10}</p>
     * <p>Paramètres : registre destination, registre A, registre B (1 octet chacun).</p>
     * <p>Exemple assembleur : {@code xor r2, r0, r1} → r2 = r0 XOR r1</p>
     */
    XOR(10);

    // -------------------------------------------------------------------------

    /** Valeur numérique de l'opcode telle qu'elle est stockée en mémoire. */
    private final int value;

    /**
     * Construit un opcode avec sa valeur numérique associée.
     *
     * @param value la valeur entière représentant cet opcode en mémoire
     */
    Opcode(int value) {
        this.value = value;
    }

    /**
     * Retourne la valeur numérique de l'opcode.
     *
     * @return la valeur entière de l'opcode (telle qu'elle est stockée en mémoire)
     */
    public int getValue() {
        return value;
    }

    /**
     * Recherche et retourne l'opcode correspondant à une valeur numérique donnée.
     *
     * <p>Cette méthode est utilisée par le processeur pour décoder les instructions
     * lues depuis la mémoire.</p>
     *
     * @param value la valeur numérique lue depuis la mémoire
     * @return l'opcode correspondant
     * @throws exception.InvalidOpcodeException si aucun opcode ne correspond à cette valeur
     */
    public static Opcode fromValue(int value) throws exception.InvalidOpcodeException {
        for (Opcode op : values()) {
            if (op.value == value) {
                return op;
            }
        }
        throw new exception.InvalidOpcodeException(value);
    }
}