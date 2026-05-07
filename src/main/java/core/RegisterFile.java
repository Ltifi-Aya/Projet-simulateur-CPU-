package core;

import exception.RegisterOutOfBoundsException;

/**
 * Représente le banc de registres du simulateur CPU.
 *
 * <p>Le processeur dispose de 16 registres généraux (r0 à r15), chacun stockant
 * une valeur sur 8 bits (type {@code byte}). Ces registres sont utilisés pour les
 * opérations arithmétiques, logiques, et pour les transferts avec la mémoire.</p>
 *
 * <p>Fonctionnalités principales :</p>
 * <ul>
 *   <li>Lecture d'un registre par son indice ({@link #get(int)})</li>
 *   <li>Écriture d'une valeur dans un registre ({@link #set(int, byte)})</li>
 *   <li>Réinitialisation de tous les registres à zéro ({@link #reset()})</li>
 * </ul>
 *
 * <p>Toute tentative d'accès à un registre avec un indice hors de [0, 15] lève une
 * {@link RegisterOutOfBoundsException}.</p>
 *
 * @author Personne 1
 * @version 1.0
 * @see RegisterOutOfBoundsException
 */
public class RegisterFile {

    /** Nombre total de registres disponibles dans le CPU. */
    public static final int NUM_REGISTERS = 16;

    /** Tableau des registres (r0 à r15), chacun sur 8 bits. */
    private final byte[] registers;

    /**
     * Construit un banc de registres avec 16 registres initialisés à zéro.
     */
    public RegisterFile() {
        this.registers = new byte[NUM_REGISTERS];
    }

    /**
     * Vérifie qu'un indice de registre est dans la plage valide [0, 15].
     *
     * @param index l'indice à valider
     * @throws RegisterOutOfBoundsException si l'indice est hors de [0, 15]
     */
    private void checkIndex(int index) throws RegisterOutOfBoundsException {
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new RegisterOutOfBoundsException(index);
        }
    }

    /**
     * Retourne la valeur contenue dans le registre spécifié.
     *
     * <p>La valeur retournée est dans la plage [-128, 127] (type {@code byte} signé Java).
     * Pour obtenir la valeur non signée, utilisez {@code value & 0xFF}.</p>
     *
     * @param index l'indice du registre à lire (doit être dans [0, 15])
     * @return la valeur stockée dans le registre
     * @throws RegisterOutOfBoundsException si l'indice est hors de [0, 15]
     */
    public byte get(int index) throws RegisterOutOfBoundsException {
        checkIndex(index);
        return registers[index];
    }

    /**
     * Écrit une valeur dans le registre spécifié.
     *
     * <p>La valeur est stockée telle quelle en tant que {@code byte} (8 bits).
     * Si vous souhaitez écrire une valeur entière, pensez à la caster :
     * {@code set(index, (byte) value)}.</p>
     *
     * @param index l'indice du registre à écrire (doit être dans [0, 15])
     * @param value la valeur à stocker dans le registre (8 bits)
     * @throws RegisterOutOfBoundsException si l'indice est hors de [0, 15]
     */
    public void set(int index, byte value) throws RegisterOutOfBoundsException {
        checkIndex(index);
        registers[index] = value;
    }

    /**
     * Réinitialise tous les registres à zéro.
     *
     * <p>Les 16 registres (r0 à r15) sont remis à la valeur 0.</p>
     */
    public void reset() {
        java.util.Arrays.fill(registers, (byte) 0);
    }

    /**
     * Retourne une représentation textuelle du banc de registres.
     *
     * <p>Affiche la valeur de chacun des 16 registres en décimal et en hexadécimal.</p>
     *
     * @return une chaîne représentant l'état de tous les registres
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RegisterFile {\n");
        for (int i = 0; i < NUM_REGISTERS; i++) {
            sb.append(String.format("  r%-2d = %4d (0x%02X)%n",
                    i, registers[i] & 0xFF, registers[i] & 0xFF));
        }
        sb.append("}");
        return sb.toString();
    }
}