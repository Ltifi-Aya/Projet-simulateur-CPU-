package exception;

/**
 * Exception levée lorsqu'une tentative d'accès à la mémoire est effectuée
 * avec une adresse hors des limites valides (0 à 65535).
 *
 * <p>La mémoire du simulateur CPU contient 64 Ko (65 536 octets).
 * Toute lecture ou écriture en dehors de cette plage lève cette exception.</p>
 *
 * @author Personne 1
 * @version 1.0
 */
public class MemoryOutOfBoundsException extends Exception {

    /** Adresse mémoire invalide qui a causé l'exception. */
    private final int address;

    /**
     * Construit une exception avec l'adresse invalide spécifiée.
     *
     * @param address l'adresse mémoire hors limites (peut être négative ou supérieure à 65535)
     */
    public MemoryOutOfBoundsException(int address) {
        super("Adresse mémoire hors limites : " + address
                + " (plage valide : 0 à 65535)");
        this.address = address;
    }

    /**
     * Retourne l'adresse mémoire invalide qui a déclenché cette exception.
     *
     * @return l'adresse hors limites
     */
    public int getAddress() {
        return address;
    }
}