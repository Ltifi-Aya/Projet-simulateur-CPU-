import core.Memory;
import exception.MemoryOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link Memory}.
 *
 * <p>Vérifie le bon fonctionnement de la mémoire 64 Ko du simulateur CPU :
 * lecture, écriture, valeurs par défaut, gestion des limites et réinitialisation.</p>
 *
 * @author Personne 1
 * @version 1.0
 */
class MemoryTest {

    /** Instance de mémoire utilisée pour chaque test (réinitialisée avant chaque test). */
    private Memory memory;

    /**
     * Initialise une nouvelle instance de {@link Memory} avant chaque test.
     */
    @BeforeEach
    void setUp() {
        memory = new Memory();
    }

    // =========================================================================
    // Tests de lecture / écriture d'un octet
    // =========================================================================

    /**
     * Vérifie qu'on peut écrire un byte et le relire à la même adresse.
     */
    @Test
    void testReadWriteByte() throws MemoryOutOfBoundsException {
        memory.writeByte(100, (byte) 42);
        assertEquals((byte) 42, memory.readByte(100),
                "La valeur lue doit être égale à la valeur écrite");

        memory.writeByte(0, (byte) -1);
        assertEquals((byte) -1, memory.readByte(0),
                "Écriture à l'adresse 0 : la valeur doit être -1 (0xFF)");

        memory.writeByte(65535, (byte) 127);
        assertEquals((byte) 127, memory.readByte(65535),
                "Écriture à l'adresse maximale 65535");
    }

    // =========================================================================
    // Tests de lecture / écriture d'un mot (16 bits)
    // =========================================================================

    /**
     * Vérifie qu'on peut écrire un mot 16 bits et le relire correctement.
     */
    @Test
    void testReadWriteWord() throws MemoryOutOfBoundsException {
        memory.writeWord(200, 0x01FF);
        assertEquals(0x01FF, memory.readWord(200),
                "Lecture du mot 0x01FF à l'adresse 200");

        // Vérification de l'encodage big-endian
        assertEquals((byte) 0x01, memory.readByte(200),
                "Octet de poids fort (adresse 200) = 0x01");
        assertEquals((byte) 0xFF, memory.readByte(201),
                "Octet de poids faible (adresse 201) = 0xFF");

        memory.writeWord(1000, 0x0000);
        assertEquals(0x0000, memory.readWord(1000),
                "Écriture et lecture de 0x0000");

        memory.writeWord(500, 0xFFFF);
        assertEquals(0xFFFF, memory.readWord(500),
                "Écriture et lecture de 0xFFFF (valeur maximale 16 bits)");
    }

    // =========================================================================
    // Test de valeur par défaut
    // =========================================================================

    /**
     * Vérifie que toutes les cellules mémoire valent 0 à la création.
     */
    @Test
    void testReadDefaultValue() throws MemoryOutOfBoundsException {
        // Les bytes Java sont initialisés à 0 par défaut dans les tableaux
        assertEquals((byte) 0, memory.readByte(0),
                "Adresse 0 : valeur par défaut doit être 0");
        assertEquals((byte) 0, memory.readByte(1000),
                "Adresse 1000 : valeur par défaut doit être 0");
        assertEquals((byte) 0, memory.readByte(65535),
                "Adresse 65535 : valeur par défaut doit être 0");
        assertEquals(0, memory.readWord(2000),
                "Mot à l'adresse 2000 : valeur par défaut doit être 0");
    }

    // =========================================================================
    // Test des adresses limites valides
    // =========================================================================

    /**
     * Vérifie que les adresses aux frontières de la mémoire (0 et 65535) sont valides.
     */
    @Test
    void testBoundaryAddresses() {
        // Adresse 0 : aucune exception
        assertDoesNotThrow(() -> memory.writeByte(0, (byte) 10),
                "L'écriture à l'adresse 0 doit fonctionner sans exception");
        assertDoesNotThrow(() -> memory.readByte(0),
                "La lecture à l'adresse 0 doit fonctionner sans exception");

        // Adresse 65535 : aucune exception
        assertDoesNotThrow(() -> memory.writeByte(65535, (byte) 20),
                "L'écriture à l'adresse 65535 doit fonctionner sans exception");
        assertDoesNotThrow(() -> memory.readByte(65535),
                "La lecture à l'adresse 65535 doit fonctionner sans exception");

        // Adresse 65534 pour un mot (65534 + 65535) : aucune exception
        assertDoesNotThrow(() -> memory.writeWord(65534, 0x1234),
                "L'écriture d'un mot à l'adresse 65534 doit fonctionner");
        assertDoesNotThrow(() -> memory.readWord(65534),
                "La lecture d'un mot à l'adresse 65534 doit fonctionner");
    }

    // =========================================================================
    // Test d'accès hors limites
    // =========================================================================

    /**
     * Vérifie qu'une {@link MemoryOutOfBoundsException} est levée lors d'accès invalides.
     */
    @Test
    void testOutOfBoundsRead() {
        // Adresse négative
        assertThrows(MemoryOutOfBoundsException.class,
                () -> memory.readByte(-1),
                "La lecture à l'adresse -1 doit lever MemoryOutOfBoundsException");

        // Adresse trop grande
        assertThrows(MemoryOutOfBoundsException.class,
                () -> memory.readByte(65536),
                "La lecture à l'adresse 65536 doit lever MemoryOutOfBoundsException");

        // Écriture hors limites
        assertThrows(MemoryOutOfBoundsException.class,
                () -> memory.writeByte(-1, (byte) 0),
                "L'écriture à l'adresse -1 doit lever MemoryOutOfBoundsException");

        assertThrows(MemoryOutOfBoundsException.class,
                () -> memory.writeByte(70000, (byte) 0),
                "L'écriture à l'adresse 70000 doit lever MemoryOutOfBoundsException");

        // Mot dont la seconde adresse est hors limites
        assertThrows(MemoryOutOfBoundsException.class,
                () -> memory.readWord(65535),
                "La lecture d'un mot à l'adresse 65535 doit lever MemoryOutOfBoundsException " +
                        "(adresse 65536 hors limites)");
    }

    // =========================================================================
    // Test de réinitialisation
    // =========================================================================

    /**
     * Vérifie que {@link Memory#reset()} remet toute la mémoire à zéro.
     */
    @Test
    void testReset() throws MemoryOutOfBoundsException {
        // Écriture de quelques valeurs
        memory.writeByte(0,     (byte) 55);
        memory.writeByte(1000,  (byte) 99);
        memory.writeByte(65535, (byte) 127);

        // Réinitialisation
        memory.reset();

        // Vérification que tout est à 0
        assertEquals((byte) 0, memory.readByte(0),
                "Après reset, l'adresse 0 doit valoir 0");
        assertEquals((byte) 0, memory.readByte(1000),
                "Après reset, l'adresse 1000 doit valoir 0");
        assertEquals((byte) 0, memory.readByte(65535),
                "Après reset, l'adresse 65535 doit valoir 0");
    }
}