package core;

/**
 * Unité Arithmétique et Logique (UAL / ALU) du simulateur CPU.
 *
 * <p>L'ALU effectue toutes les opérations arithmétiques et logiques
 * sur des valeurs 8 bits (octets non signés, plage [0, 255]).
 * Les résultats sont tronqués à 8 bits sauf pour la multiplication,
 * dont le résultat 16 bits est retourné sous forme de tableau de deux octets.</p>
 *
 * <p>Opérations disponibles :</p>
 * <ul>
 *   <li>{@link #add(byte, byte)} — Addition (résultat tronqué à 8 bits)</li>
 *   <li>{@link #sub(byte, byte)} — Soustraction (résultat tronqué à 8 bits)</li>
 *   <li>{@link #mul(byte, byte)} — Multiplication (résultat 16 bits → 2 octets)</li>
 *   <li>{@link #div(byte, byte)} — Division entière (quotient + reste)</li>
 *   <li>{@link #and(byte, byte)} — ET binaire</li>
 *   <li>{@link #or(byte, byte)}  — OU binaire</li>
 *   <li>{@link #xor(byte, byte)} — OU exclusif binaire</li>
 * </ul>
 *
 * <p>L'ALU est sans état : chaque méthode est indépendante et peut être
 * appelée dans n'importe quel ordre.</p>
 *
 * @author Personne 2
 * @version 1.0
 */
public class ALU {

    /**
     * Additionne deux valeurs 8 bits non signées.
     *
     * <p>Le résultat est tronqué à 8 bits (overflow silencieux).
     * Par exemple, {@code add((byte)200, (byte)100)} retourne {@code (byte)44}
     * car {@code 300 & 0xFF = 44}.</p>
     *
     * @param a premier opérande (8 bits)
     * @param b second opérande (8 bits)
     * @return le résultat de a + b, tronqué à 8 bits
     */
    public byte add(byte a, byte b) {
        return (byte) ((a & 0xFF) + (b & 0xFF));
    }

    /**
     * Soustrait {@code b} de {@code a} sur 8 bits.
     *
     * <p>Le résultat est tronqué à 8 bits (underflow silencieux avec complément à 2).
     * Par exemple, {@code sub((byte)5, (byte)10)} retourne {@code (byte)251}
     * car {@code (5 - 10) & 0xFF = 251}.</p>
     *
     * @param a opérande de gauche (8 bits)
     * @param b opérande à soustraire (8 bits)
     * @return le résultat de a - b, tronqué à 8 bits
     */
    public byte sub(byte a, byte b) {
        return (byte) ((a & 0xFF) - (b & 0xFF));
    }

    /**
     * Multiplie deux valeurs 8 bits non signées.
     *
     * <p>Le résultat d'une multiplication peut dépasser 8 bits (jusqu'à 255 × 255 = 65025).
     * Il est donc retourné sous forme d'un tableau de deux octets :</p>
     * <ul>
     *   <li>Index 0 : octet de poids fort (bits 15–8)</li>
     *   <li>Index 1 : octet de poids faible (bits 7–0)</li>
     * </ul>
     *
     * <p>Exemple : {@code mul((byte)200, (byte)200)} → résultat = 40000 →
     * {@code [0x9C, 0x40]} soit {@code [156, 64]}.</p>
     *
     * @param a premier facteur (8 bits non signé)
     * @param b second facteur (8 bits non signé)
     * @return tableau de 2 octets : [poids fort, poids faible] du résultat 16 bits
     */
    public byte[] mul(byte a, byte b) {
        int result = (a & 0xFF) * (b & 0xFF);
        byte high = (byte) ((result >> 8) & 0xFF);
        byte low  = (byte) (result & 0xFF);
        return new byte[]{high, low};
    }

    /**
     * Divise {@code a} par {@code b} en entier non signé (division euclidienne).
     *
     * <p>Le résultat est retourné sous forme d'un tableau de deux octets :</p>
     * <ul>
     *   <li>Index 0 : quotient de la division</li>
     *   <li>Index 1 : reste de la division (modulo)</li>
     * </ul>
     *
     * @param a dividende (8 bits non signé)
     * @param b diviseur (8 bits non signé, doit être ≠ 0)
     * @return tableau de 2 octets : [quotient, reste]
     * @throws ArithmeticException si {@code b} vaut 0 (division par zéro)
     */
    public byte[] div(byte a, byte b) {
        int bVal = b & 0xFF;
        if (bVal == 0) {
            throw new ArithmeticException("Division par zéro");
        }
        int aVal     = a & 0xFF;
        byte quotient = (byte) (aVal / bVal);
        byte remainder = (byte) (aVal % bVal);
        return new byte[]{quotient, remainder};
    }

    /**
     * Effectue un ET binaire (AND) bit à bit entre deux valeurs 8 bits.
     *
     * <p>Exemple : {@code and((byte)0b10101010, (byte)0b11001100)} → {@code (byte)0b10001000}.</p>
     *
     * @param a premier opérande (8 bits)
     * @param b second opérande (8 bits)
     * @return le résultat de a AND b (8 bits)
     */
    public byte and(byte a, byte b) {
        return (byte) (a & b);
    }

    /**
     * Effectue un OU binaire (OR) bit à bit entre deux valeurs 8 bits.
     *
     * <p>Exemple : {@code or((byte)0b10100000, (byte)0b00001010)} → {@code (byte)0b10101010}.</p>
     *
     * @param a premier opérande (8 bits)
     * @param b second opérande (8 bits)
     * @return le résultat de a OR b (8 bits)
     */
    public byte or(byte a, byte b) {
        return (byte) (a | b);
    }

    /**
     * Effectue un OU exclusif (XOR) bit à bit entre deux valeurs 8 bits.
     *
     * <p>Exemple : {@code xor((byte)0b11001100, (byte)0b10101010)} → {@code (byte)0b01100110}.</p>
     *
     * @param a premier opérande (8 bits)
     * @param b second opérande (8 bits)
     * @return le résultat de a XOR b (8 bits)
     */
    public byte xor(byte a, byte b) {
        return (byte) (a ^ b);
    }
}