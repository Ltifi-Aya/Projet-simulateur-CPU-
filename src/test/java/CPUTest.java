import core.CPU;
import core.Memory;
import exception.InvalidOpcodeException;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link CPU}.
 *
 * <p>Vérifie le bon fonctionnement du CPU :
 * exécution des instructions, gestion des registres,
 * branchements conditionnels, et la boucle
 * Fetch/Decode/Execute.</p>
 *
 * @author Équipe de développement
 * @version 1.0
 */
class CPUTest {

    /** Instance de la mémoire partagée. */
    private Memory memory;

    /** Instance du CPU. */
    private CPU cpu;

    /**
     * Initialise une nouvelle mémoire et un nouveau CPU avant chaque test.
     */
    @BeforeEach
    void setUp() {
        memory = new Memory();
        cpu = new CPU(memory);
    }

    /**
     * Teste que le CPU s'arrête correctement avec l'instruction BREAK.
     * Cela vérifie que la boucle Fetch/Decode/Execute s'arrête bien.
     */
    @Test
    void testBreak() throws Exception {
        // Écrit l'instruction BREAK (opcode 0) en mémoire
        memory.writeByte(0, (byte) 0);

        // Lance l'exécution
        cpu.run();

        // Vérifie que le CPU s'est arrêté
        assertFalse(cpu.isRunning(), "CPU devrait être arrêté après BREAK");
        // PC devrait être à 1 (après avoir lu le BREAK)
        assertEquals(1, cpu.getPC(), "PC devrait être à 1 après BREAK");
    }

    /**
     * Teste l'instruction LOAD_CONST qui charge une valeur constante dans un registre.
     * Format : OPCODE + REGISTRE + VALEUR
     */
    @Test
    void testLoadConst() throws Exception {
        // LOAD_CONST r0, 42 suivie de BREAK
        memory.writeByte(0, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(1, (byte) 0);    // registre r0
        memory.writeByte(2, (byte) 42);   // valeur 42
        memory.writeByte(3, (byte) 0);    // BREAK opcode

        // Lance l'exécution
        cpu.run();

        // Vérifie que la valeur a été chargée dans r0
        assertEquals((byte) 42, cpu.getRegister(0), "r0 devrait contenir 42");
    }

    /**
     * Teste l'instruction STORE qui écrit la valeur d'un registre en mémoire.
     * Format : OPCODE + REGISTRE + ADRESSE_H + ADRESSE_L
     */
    @Test
    void testStore() throws Exception {
        // LOAD_CONST r1, 99 suivi de STORE r1, @300 suivi de BREAK
        memory.writeByte(0, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(1, (byte) 1);    // registre r1
        memory.writeByte(2, (byte) 99);   // valeur 99
        memory.writeByte(3, (byte) 3);    // STORE opcode
        memory.writeByte(4, (byte) 1);    // registre r1
        memory.writeByte(5, (byte) 0x01); // adresse haute (300 = 0x012C)
        memory.writeByte(6, (byte) 0x2C); // adresse basse
        memory.writeByte(7, (byte) 0);    // BREAK opcode

        // Lance l'exécution
        cpu.run();

        // Vérifie que la valeur a été stockée à l'adresse 300
        assertEquals((byte) 99, memory.readByte(300), "mémoire[300] devrait contenir 99");
    }

    /**
     * Teste l'instruction JUMP qui saute à une adresse arbitraire.
     * Format : OPCODE + ADRESSE_H + ADRESSE_L
     */
    @Test
    void testJump() throws Exception {
        // JUMP @5 suivi de 2 instructures de "padding" puis BREAK
        memory.writeByte(0, (byte) 12);   // JUMP opcode
        memory.writeByte(1, (byte) 0x00); // adresse haute (5)
        memory.writeByte(2, (byte) 0x05); // adresse basse
        memory.writeByte(3, (byte) 1);    // LOAD_CONST (padding)
        memory.writeByte(4, (byte) 0);    // registre (padding)
        memory.writeByte(5, (byte) 0);    // BREAK opcode

        // Lance l'exécution
        cpu.run();

        // Vérifie que PC est à 6 (après BREAK à l'adresse 5)
        assertEquals(6, cpu.getPC(), "PC devrait être à 6 après BREAK");
    }

    /**
     * Teste l'instruction BEQ (Branch if Equal) — cas où le saut doit être effectué.
     * Format : OPCODE + REG_A + REG_B + ADRESSE_H + ADRESSE_L
     */
    @Test
    void testBeq_saut() throws Exception {
        // LOAD_CONST r0, 5 suivi de LOAD_CONST r1, 5 suivi de BEQ r0, r1, @10 suivi de BREAK
        memory.writeByte(0, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(1, (byte) 0);    // registre r0
        memory.writeByte(2, (byte) 5);    // valeur 5
        memory.writeByte(3, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(4, (byte) 1);    // registre r1
        memory.writeByte(5, (byte) 5);    // valeur 5
        memory.writeByte(6, (byte) 13);   // BEQ opcode
        memory.writeByte(7, (byte) 0);    // reg A (r0)
        memory.writeByte(8, (byte) 1);    // reg B (r1)
        memory.writeByte(9, (byte) 0x00); // adresse haute (10)
        memory.writeByte(10, (byte) 0x0A); // adresse basse
        memory.writeByte(11, (byte) 1);   // LOAD_CONST (ne devrait pas être exécuté)
        memory.writeByte(12, (byte) 2);   // registre (padding)
        memory.writeByte(13, (byte) 99);  // valeur (padding)
        memory.writeByte(14, (byte) 0);   // BREAK opcode

        // Lance l'exécution
        cpu.run();

        // Vérifie que le saut a été effectué (r2 ne devrait pas contenir 99)
        assertNotEquals((byte) 99, cpu.getRegister(2), "BEQ devrait sauter, LOAD_CONST r2,99 ne devrait pas s'exécuter");
    }

    /**
     * Teste l'instruction BEQ (Branch if Equal) — cas où le saut ne doit pas être effectué.
     * Format : OPCODE + REG_A + REG_B + ADRESSE_H + ADRESSE_L
     */
    @Test
    void testBeq_pasSaut() throws Exception {
        // LOAD_CONST r0, 5 suivi de LOAD_CONST r1, 6 suivi de BEQ r0, r1, @10 suivi de LOAD_CONST r2, 99 suivi de BREAK
        memory.writeByte(0, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(1, (byte) 0);    // registre r0
        memory.writeByte(2, (byte) 5);    // valeur 5
        memory.writeByte(3, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(4, (byte) 1);    // registre r1
        memory.writeByte(5, (byte) 6);    // valeur 6
        memory.writeByte(6, (byte) 13);   // BEQ opcode
        memory.writeByte(7, (byte) 0);    // reg A (r0)
        memory.writeByte(8, (byte) 1);    // reg B (r1)
        memory.writeByte(9, (byte) 0x00); // adresse haute
        memory.writeByte(10, (byte) 0x0A); // adresse basse
        memory.writeByte(11, (byte) 1);   // LOAD_CONST opcode (devrait s'exécuter)
        memory.writeByte(12, (byte) 2);   // registre r2
        memory.writeByte(13, (byte) 99);  // valeur 99
        memory.writeByte(14, (byte) 0);   // BREAK opcode

        // Lance l'exécution
        cpu.run();

        // Vérifie que le saut n'a pas été effectué (r2 devrait contenir 99)
        assertEquals((byte) 99, cpu.getRegister(2), "BEQ ne devrait pas sauter, LOAD_CONST r2,99 devrait s'exécuter");
    }

    /**
     * Teste l'instruction BNE (Branch if Not Equal) — cas où le saut doit être effectué.
     * Format : OPCODE + REG_A + REG_B + ADRESSE_H + ADRESSE_L
     */
    @Test
    void testBne_saut() throws Exception {
        // LOAD_CONST r0, 5 suivi de LOAD_CONST r1, 6 suivi de BNE r0, r1, @10 suivi de BREAK
        memory.writeByte(0, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(1, (byte) 0);    // registre r0
        memory.writeByte(2, (byte) 5);    // valeur 5
        memory.writeByte(3, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(4, (byte) 1);    // registre r1
        memory.writeByte(5, (byte) 6);    // valeur 6
        memory.writeByte(6, (byte) 14);   // BNE opcode
        memory.writeByte(7, (byte) 0);    // reg A (r0)
        memory.writeByte(8, (byte) 1);    // reg B (r1)
        memory.writeByte(9, (byte) 0x00); // adresse haute
        memory.writeByte(10, (byte) 0x0A); // adresse basse
        memory.writeByte(11, (byte) 1);   // LOAD_CONST (ne devrait pas être exécuté)
        memory.writeByte(12, (byte) 2);   // registre (padding)
        memory.writeByte(13, (byte) 99);  // valeur (padding)
        memory.writeByte(14, (byte) 0);   // BREAK opcode

        // Lance l'exécution
        cpu.run();

        // Vérifie que le saut a été effectué (r2 ne devrait pas contenir 99)
        assertNotEquals((byte) 99, cpu.getRegister(2), "BNE devrait sauter, LOAD_CONST r2,99 ne devrait pas s'exécuter");
    }

    /**
     * Teste l'instruction BNE (Branch if Not Equal) — cas où le saut ne doit pas être effectué.
     * Format : OPCODE + REG_A + REG_B + ADRESSE_H + ADRESSE_L
     */
    @Test
    void testBne_pasSaut() throws Exception {
        // LOAD_CONST r0, 5 suivi de LOAD_CONST r1, 5 suivi de BNE r0, r1, @10 suivi de LOAD_CONST r2, 99 suivi de BREAK
        memory.writeByte(0, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(1, (byte) 0);    // registre r0
        memory.writeByte(2, (byte) 5);    // valeur 5
        memory.writeByte(3, (byte) 1);    // LOAD_CONST opcode
        memory.writeByte(4, (byte) 1);    // registre r1
        memory.writeByte(5, (byte) 5);    // valeur 5
        memory.writeByte(6, (byte) 14);   // BNE opcode
        memory.writeByte(7, (byte) 0);    // reg A (r0)
        memory.writeByte(8, (byte) 1);    // reg B (r1)
        memory.writeByte(9, (byte) 0x00); // adresse haute
        memory.writeByte(10, (byte) 0x0A); // adresse basse
        memory.writeByte(11, (byte) 1);   // LOAD_CONST opcode (devrait s'exécuter)
        memory.writeByte(12, (byte) 2);   // registre r2
        memory.writeByte(13, (byte) 99);  // valeur 99
        memory.writeByte(14, (byte) 0);   // BREAK opcode

        // Lance l'exécution
        cpu.run();

        // Vérifie que le saut n'a pas été effectué (r2 devrait contenir 99)
        assertEquals((byte) 99, cpu.getRegister(2), "BNE ne devrait pas sauter, LOAD_CONST r2,99 devrait s'exécuter");
    }

}
