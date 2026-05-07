package exception;

/**
 * Exception levée lorsqu'une tentative d'accès à un registre est effectuée
 * avec un numéro de registre hors des limites valides (0 à 15).
 *
 * <p>Le simulateur CPU dispose de 16 registres numérotés de 0 à 15.
 * Toute lecture ou écriture avec un indice en dehors de cette plage
 * lève cette exception.</p>
 *
 * @author Personne 1
 * @version 1.0
 */
public class RegisterOutOfBoundsException extends Exception {

    /** Numéro de registre invalide qui a causé l'exception. */
    private final int registerIndex;

    /**
     * Construit une exception avec le numéro de registre invalide spécifié.
     *
     * @param registerIndex le numéro de registre hors limites (valide : 0 à 15)
     */
    public RegisterOutOfBoundsException(int registerIndex) {
        super("Numéro de registre hors limites : " + registerIndex
                + " (plage valide : 0 à 15)");
        this.registerIndex = registerIndex;
    }

    /**
     * Retourne le numéro de registre invalide qui a déclenché cette exception.
     *
     * @return le numéro de registre hors limites
     */
    public int getRegisterIndex() {
        return registerIndex;
    }
}