import core.RegisterFile;
import exception.RegisterOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link RegisterFile}.
 *
 * <p>Vérifie le bon fonctionnement du banc de 16 registres 8 bits du simulateur CPU :
 * lecture, écriture, réinitialisation et gestion des limites.</p>
 *
 * @author Personne 1
 * @version 1.0
 */
class RegisterFileTest {

    /** Instance du banc de registres utilisée pour chaque test. */
    private RegisterFile registerFile;

    /**
     * Initialise un nouveau {@link RegisterFile} avant chaque test.
     */
    @BeforeEach
    void setUp() {
        registerFile = new RegisterFile();
    }

    // =========================================================================
    // Test de lecture / écriture
    // =========================================================================

    /**
     * Vérifie qu'on peut écrire une valeur dans un registre et la relire.
     */
    @Test
    void testGetSet() throws RegisterOutOfBoundsException {
        // Écriture et lecture sur r0
        registerFile.set(0, (byte) 42);
        assertEquals((byte) 42, registerFile.get(0),
                "r0 doit contenir 42 après set(0, 42)");

        // Écriture et lecture sur r15 (dernier registre valide)
        registerFile.set(15, (byte) -1);
        assertEquals((byte) -1, registerFile.get(15),
                "r15 doit contenir -1 (0xFF) après set(15, -1)");

        // Écriture sur plusieurs registres simultanément
        for (int i = 0; i < 16; i++) {
            registerFile.set(i, (byte) i);
        }
        for (int i = 0; i < 16; i++) {
            assertEquals((byte) i, registerFile.get(i),
                    "r" + i + " doit contenir " + i);
        }
    }

    // =========================================================================
    // Test de réinitialisation
    // =========================================================================

    /**
     * Vérifie que {@link RegisterFile#reset()} remet tous les registres à 0.
     */
    @Test
    void testReset() throws RegisterOutOfBoundsException {
        // Écriture de valeurs dans plusieurs registres
        registerFile.set(0,  (byte) 10);
        registerFile.set(5,  (byte) 50);
        registerFile.set(15, (byte) 99);

        // Réinitialisation
        registerFile.reset();

        // Tous les registres doivent être à 0
        for (int i = 0; i < 16; i++) {
            assertEquals((byte) 0, registerFile.get(i),
                    "Après reset, r" + i + " doit valoir 0");
        }
    }

    // =========================================================================
    // Test d'accès hors limites
    // =========================================================================

    /**
     * Vérifie qu'une {@link RegisterOutOfBoundsException} est levée lors d'accès invalides.
     */
    @Test
    void testOutOfBounds() {
        // Index négatif
        assertThrows(RegisterOutOfBoundsException.class,
                () -> registerFile.get(-1),
                "get(-1) doit lever RegisterOutOfBoundsException");

        assertThrows(RegisterOutOfBoundsException.class,
                () -> registerFile.set(-1, (byte) 0),
                "set(-1, ...) doit lever RegisterOutOfBoundsException");

        // Index trop grand (16 et au-delà)
        assertThrows(RegisterOutOfBoundsException.class,
                () -> registerFile.get(16),
                "get(16) doit lever RegisterOutOfBoundsException (max = 15)");

        assertThrows(RegisterOutOfBoundsException.class,
                () -> registerFile.set(16, (byte) 0),
                "set(16, ...) doit lever RegisterOutOfBoundsException (max = 15)");

        assertThrows(RegisterOutOfBoundsException.class,
                () -> registerFile.get(100),
                "get(100) doit lever RegisterOutOfBoundsException");
    }
}