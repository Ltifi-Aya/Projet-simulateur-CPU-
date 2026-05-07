package core;

import exception.MemoryOutOfBoundsException;

/**
 * Représente la mémoire principale du simulateur CPU.
 *
 * <p>La mémoire est composée de 65 536 cellules (64 Ko), chacune stockant
 * une valeur sur 8 bits (type {@code byte}). Elle est adressable via des
 * entiers de 0 à 65 535 (adressage 16 bits).</p>
 *
 * <p>Fonctionnalités principales :</p>
 * <ul>
 *   <li>Lecture d'un octet à une adresse donnée ({@link #readByte(int)})</li>
 *   <li>Écriture d'un octet à une adresse donnée ({@link #writeByte(int, byte)})</li>
 *   <li>Lecture d'un mot de 16 bits (2 octets) ({@link #readWord(int)})</li>
 *   <li>Écriture d'un mot de 16 bits ({@link #writeWord(int, int)})</li>
 *   <li>Réinitialisation complète de la mémoire à zéro ({@link #reset()})</li>
 * </ul>
 *
 * <p>Toute tentative d'accès hors de la plage [0, 65535] lève une
 * {@link MemoryOutOfBoundsException}.</p>
 *
 * @author Personne 1
 * @version 1.0
 * @see MemoryOutOfBoundsException
 */
public class Memory {

    /** Taille totale de la mémoire en octets : 64 Ko = 65 536 octets. */
    public static final int MEMORY_SIZE = 65536;

    /** Tableau de bytes représentant les cellules mémoire. */
    private final byte[] data;

    /**
     * Construit une nouvelle mémoire de 64 Ko, initialisée à zéro.
     */
    public Memory() {
        this.data = new byte[MEMORY_SIZE];
    }

    /**
     * Vérifie qu'une adresse est dans la plage valide [0, MEMORY_SIZE - 1].
     *
     * @param address l'adresse à valider
     * @throws MemoryOutOfBoundsException si l'adresse est hors limites
     */
    private void checkAddress(int address) throws MemoryOutOfBoundsException {
        if (address < 0 || address >= MEMORY_SIZE) {
            throw new MemoryOutOfBoundsException(address);
        }
    }

    /**
     * Lit un octet (8 bits) à l'adresse mémoire spécifiée.
     *
     * <p>La valeur retournée est dans la plage [-128, 127] (type {@code byte} signé Java).
     * Pour obtenir une valeur non signée [0, 255], utilisez
     * {@code data & 0xFF}.</p>
     *
     * @param address l'adresse mémoire à lire (doit être dans [0, 65535])
     * @return le byte stocké à cette adresse
     * @throws MemoryOutOfBoundsException si l'adresse est hors limites
     */
    public byte readByte(int address) throws MemoryOutOfBoundsException {
        checkAddress(address);
        return data[address];
    }

    /**
     * Écrit un octet (8 bits) à l'adresse mémoire spécifiée.
     *
     * <p>Seuls les 8 bits de poids faible de la valeur {@code value} sont écrits.
     * Les bits supérieurs sont ignorés (troncature automatique par le cast en byte).</p>
     *
     * @param address l'adresse mémoire à écrire (doit être dans [0, 65535])
     * @param value   la valeur à écrire (seuls les 8 bits de poids faible sont conservés)
     * @throws MemoryOutOfBoundsException si l'adresse est hors limites
     */
    public void writeByte(int address, byte value) throws MemoryOutOfBoundsException {
        checkAddress(address);
        data[address] = value;
    }

    /**
     * Lit un mot de 16 bits (2 octets consécutifs) à partir de l'adresse spécifiée.
     *
     * <p>Le premier octet (à {@code address}) est le poids fort (big-endian),
     * le second (à {@code address + 1}) est le poids faible.</p>
     *
     * <p>Exemple : si {@code data[100] = 0x01} et {@code data[101] = 0xFF},
     * alors {@code readWord(100)} retourne {@code 0x01FF = 511}.</p>
     *
     * @param address l'adresse de début (les adresses {@code address} et {@code address+1}
     *                doivent être dans [0, 65535])
     * @return la valeur 16 bits lue (dans [0, 65535])
     * @throws MemoryOutOfBoundsException si l'une des deux adresses est hors limites
     */
    public int readWord(int address) throws MemoryOutOfBoundsException {
        checkAddress(address);
        checkAddress(address + 1);
        int high = (data[address] & 0xFF) << 8;
        int low  = data[address + 1] & 0xFF;
        return high | low;
    }

    /**
     * Écrit un mot de 16 bits (2 octets consécutifs) à partir de l'adresse spécifiée.
     *
     * <p>L'écriture est effectuée en big-endian : l'octet de poids fort est écrit
     * à {@code address}, l'octet de poids faible à {@code address + 1}.</p>
     *
     * <p>Seuls les 16 bits de poids faible de {@code value} sont utilisés.</p>
     *
     * @param address l'adresse de début (les adresses {@code address} et {@code address+1}
     *                doivent être dans [0, 65535])
     * @param value   la valeur 16 bits à écrire (seuls les 16 bits de poids faible
     *                sont conservés)
     * @throws MemoryOutOfBoundsException si l'une des deux adresses est hors limites
     */
    public void writeWord(int address, int value) throws MemoryOutOfBoundsException {
        checkAddress(address);
        checkAddress(address + 1);
        data[address]     = (byte) ((value >> 8) & 0xFF);
        data[address + 1] = (byte) (value & 0xFF);
    }

    /**
     * Réinitialise toute la mémoire à zéro.
     *
     * <p>Toutes les cellules mémoire (65 536 octets) sont remises à la valeur 0.</p>
     */
    public void reset() {
        java.util.Arrays.fill(data, (byte) 0);
    }

    /**
     * Retourne une représentation textuelle de la mémoire sous forme hexadécimale.
     *
     * <p>Affiche uniquement les adresses dont la valeur est non nulle,
     * pour une meilleure lisibilité.</p>
     *
     * @return une chaîne représentant les cellules mémoire non nulles
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Memory {\n");
        for (int i = 0; i < MEMORY_SIZE; i++) {
            if (data[i] != 0) {
                sb.append(String.format("  [0x%04X] = 0x%02X (%d)%n",
                        i, data[i] & 0xFF, data[i] & 0xFF));
            }
        }
        sb.append("}");
        return sb.toString();
    }
}