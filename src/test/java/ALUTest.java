import core.ALU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link ALU}.
 *
 * <p>Vérifie le bon fonctionnement de toutes les opérations arithmétiques
 * et logiques du simulateur CPU : addition, soustraction, multiplication,
 * division, ET, OU et OU exclusif.</p>
 *
 * @author Personne 2
 * @version 1.0
 */
class ALUTest {

    /** Instance de l'ALU utilisée pour chaque test. */
    private ALU alu;

    /**
     * Initialise une nouvelle instance d'ALU avant chaque test.
     */
    @BeforeEach
    void setUp() {
        alu = new ALU();
    }

    // =========================================================================
    // Tests Addition
    // =========================================================================

    /**
     * Vérifie que l'addition de deux valeurs simples fonctionne correctement.
     */
    @Test
    void testAdd() {
        assertEquals((byte) 10, alu.add((byte) 3, (byte) 7),
                "3 + 7 = 10");
        assertEquals((byte) 0, alu.add((byte) 0, (byte) 0),
                "0 + 0 = 0");
        assertEquals((byte) 255, alu.add((byte) 200, (byte) 55),
                "200 + 55 = 255 (valeur max non signée)");
    }

    /**
     * Vérifie que l'addition avec overflow tronque correctement le résultat à 8 bits.
     */
    @Test
    void testAddOverflow() {
        // 200 + 100 = 300, tronqué à 8 bits → 300 & 0xFF = 44
        assertEquals((byte) 44, alu.add((byte) 200, (byte) 100),
                "200 + 100 = 300 → overflow → 44 (tronqué à 8 bits)");

        // 255 + 1 = 256 → tronqué à 0
        assertEquals((byte) 0, alu.add((byte) 255, (byte) 1),
                "255 + 1 = 256 → overflow → 0");

        // 255 + 255 = 510 → tronqué à 254
        assertEquals((byte) 254, alu.add((byte) 255, (byte) 255),
                "255 + 255 = 510 → overflow → 254");
    }

    // =========================================================================
    // Tests Soustraction
    // =========================================================================

    /**
     * Vérifie que la soustraction fonctionne correctement.
     */
    @Test
    void testSub() {
        assertEquals((byte) 3, alu.sub((byte) 10, (byte) 7),
                "10 - 7 = 3");
        assertEquals((byte) 0, alu.sub((byte) 5, (byte) 5),
                "5 - 5 = 0");
        // 5 - 10 = -5 → en complément à 2 sur 8 bits → 251
        assertEquals((byte) 251, alu.sub((byte) 5, (byte) 10),
                "5 - 10 = -5 → tronqué à 251 (0xFB en non signé)");
    }

    // =========================================================================
    // Tests Multiplication
    // =========================================================================

    /**
     * Vérifie la multiplication avec un résultat tenant sur 8 bits.
     */
    @Test
    void testMul() {
        byte[] result = alu.mul((byte) 3, (byte) 4);
        assertEquals(2, result.length,
                "Le résultat de mul doit être un tableau de 2 bytes");
        assertEquals((byte) 0, result[0],
                "3 × 4 = 12 : poids fort = 0");
        assertEquals((byte) 12, result[1],
                "3 × 4 = 12 : poids faible = 12");

        byte[] zero = alu.mul((byte) 0, (byte) 255);
        assertEquals((byte) 0, zero[0], "0 × 255 : poids fort = 0");
        assertEquals((byte) 0, zero[1], "0 × 255 : poids faible = 0");
    }

    /**
     * Vérifie la multiplication avec un résultat dépassant 8 bits (résultat 16 bits).
     */
    @Test
    void testMulLargeResult() {
        // 200 × 200 = 40000 = 0x9C40
        byte[] result = alu.mul((byte) 200, (byte) 200);
        assertEquals((byte) 0x9C, result[0],
                "200 × 200 = 40000 = 0x9C40 : poids fort = 0x9C");
        assertEquals((byte) 0x40, result[1],
                "200 × 200 = 40000 = 0x9C40 : poids faible = 0x40");

        // 255 × 255 = 65025 = 0xFE01
        byte[] max = alu.mul((byte) 255, (byte) 255);
        assertEquals((byte) 0xFE, max[0],
                "255 × 255 = 65025 = 0xFE01 : poids fort = 0xFE");
        assertEquals((byte) 0x01, max[1],
                "255 × 255 = 65025 = 0xFE01 : poids faible = 0x01");
    }

    // =========================================================================
    // Tests Division
    // =========================================================================

    /**
     * Vérifie la division entière (quotient et reste).
     */
    @Test
    void testDiv() {
        byte[] result = alu.div((byte) 10, (byte) 3);
        assertEquals(2, result.length,
                "Le résultat de div doit être un tableau de 2 bytes");
        assertEquals((byte) 3, result[0],
                "10 / 3 = 3 (quotient)");
        assertEquals((byte) 1, result[1],
                "10 % 3 = 1 (reste)");

        byte[] exact = alu.div((byte) 100, (byte) 10);
        assertEquals((byte) 10, exact[0],
                "100 / 10 = 10 (quotient)");
        assertEquals((byte) 0, exact[1],
                "100 % 10 = 0 (reste)");

        byte[] byOne = alu.div((byte) 42, (byte) 1);
        assertEquals((byte) 42, byOne[0],
                "42 / 1 = 42 (quotient)");
        assertEquals((byte) 0, byOne[1],
                "42 % 1 = 0 (reste)");
    }

    /**
     * Vérifie qu'une {@link ArithmeticException} est levée lors d'une division par zéro.
     */
    @Test
    void testDivByZero() {
        assertThrows(ArithmeticException.class,
                () -> alu.div((byte) 10, (byte) 0),
                "La division par zéro doit lever ArithmeticException");
    }

    // =========================================================================
    // Tests Opérations logiques
    // =========================================================================

    /**
     * Vérifie l'opération ET binaire.
     */
    @Test
    void testAnd() {
        // 0b10101010 AND 0b11001100 = 0b10001000 = 0x88 = -120 signé
        assertEquals((byte) 0b10001000, alu.and((byte) 0b10101010, (byte) 0b11001100),
                "0xAA AND 0xCC = 0x88");
        assertEquals((byte) 0, alu.and((byte) 0xFF, (byte) 0x00),
                "0xFF AND 0x00 = 0x00");
        assertEquals((byte) 0xFF, alu.and((byte) 0xFF, (byte) 0xFF),
                "0xFF AND 0xFF = 0xFF");
    }

    /**
     * Vérifie l'opération OU binaire.
     */
    @Test
    void testOr() {
        // 0b10100000 OR 0b00001010 = 0b10101010 = 0xAA
        assertEquals((byte) 0b10101010, alu.or((byte) 0b10100000, (byte) 0b00001010),
                "0xA0 OR 0x0A = 0xAA");
        assertEquals((byte) 0xFF, alu.or((byte) 0xFF, (byte) 0x00),
                "0xFF OR 0x00 = 0xFF");
        assertEquals((byte) 0xFF, alu.or((byte) 0x0F, (byte) 0xF0),
                "0x0F OR 0xF0 = 0xFF");
        assertEquals((byte) 0, alu.or((byte) 0, (byte) 0),
                "0x00 OR 0x00 = 0x00");
    }

    /**
     * Vérifie l'opération OU exclusif (XOR).
     */
    @Test
    void testXor() {
        // 0b11001100 XOR 0b10101010 = 0b01100110 = 0x66
        assertEquals((byte) 0b01100110, alu.xor((byte) 0b11001100, (byte) 0b10101010),
                "0xCC XOR 0xAA = 0x66");
        assertEquals((byte) 0, alu.xor((byte) 0xFF, (byte) 0xFF),
                "0xFF XOR 0xFF = 0x00 (un nombre XOR lui-même = 0)");
        assertEquals((byte) 0xFF, alu.xor((byte) 0x00, (byte) 0xFF),
                "0x00 XOR 0xFF = 0xFF");
        assertEquals((byte) 0xFF, alu.xor((byte) 0x0F, (byte) 0xF0),
                "0x0F XOR 0xF0 = 0xFF");
    }
}