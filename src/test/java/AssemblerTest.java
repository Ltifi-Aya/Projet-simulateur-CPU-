import assembler.Assembler;
import core.CPU;
import core.Memory;
import exception.MemoryOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link Assembler}.
 *
 * <p>Vérifie le bon fonctionnement de l'assembleur :
 * parsing des instructions, gestion des labels,
 * encodage en mémoire (big-endian), et résolution
 * des références aux labels.</p>
 *
 * @author Équipe de développement
 * @version 1.0
 */
class AssemblerTest {

    /** Instance de la mémoire partagée. */
    private Memory memory;

    /** Instance de l'assembleur. */
    private Assembler assembler;

    /**
     * Initialise une nouvelle mémoire et un nouvel assembleur avant chaque test.
     */
    @BeforeEach
    void setUp() {
        memory = new Memory();
        assembler = new Assembler(memory);
    }

    /**
     * Teste l'assemblage de l'instruction LOAD_CONST.
     * Encode : OPCODE + REGISTRE + VALEUR (3 octets)
     */
    @Test
    void testLoadConst() throws MemoryOutOfBoundsException {
        String programme = "LOAD_CONST r0, 5\n";
        assembler.assemble(programme);

        // Vérifie que les 3 octets ont été écrits
        assertEquals((byte) 1, memory.readByte(0), "Opcode LOAD_CONST");
        assertEquals((byte) 0, memory.readByte(1), "Registre r0");
        assertEquals((byte) 5, memory.readByte(2), "Valeur 5");
    }

    /**
     * Teste l'assemblage de l'instruction LOAD_MEM.
     * Encode : OPCODE + REGISTRE + ADRESSE_H + ADRESSE_L (4 octets)
     */
    @Test
    void testLoadMem() throws MemoryOutOfBoundsException {
        String programme = "LOAD_MEM r1, @300\n";
        assembler.assemble(programme);

        // Vérifie les octets
        assertEquals((byte) 2, memory.readByte(0), "Opcode LOAD_MEM");
        assertEquals((byte) 1, memory.readByte(1), "Registre r1");
        // 300 = 0x012C → octet fort = 0x01, octet faible = 0x2C
        assertEquals((byte) 0x01, memory.readByte(2), "Octet fort adresse");
        assertEquals((byte) 0x2C, memory.readByte(3), "Octet faible adresse");
    }

    /**
     * Teste l'assemblage de l'instruction STORE.
     * Encode : OPCODE + REGISTRE + ADRESSE_H + ADRESSE_L (4 octets)
     */
    @Test
    void testStore() throws MemoryOutOfBoundsException {
        String programme = "STORE r2, @400\n";
        assembler.assemble(programme);

        // Vérifie les octets
        assertEquals((byte) 3, memory.readByte(0), "Opcode STORE");
        assertEquals((byte) 2, memory.readByte(1), "Registre r2");
        // 400 = 0x0190 → octet fort = 0x01, octet faible = 0x90
        assertEquals((byte) 0x01, memory.readByte(2), "Octet fort adresse");
        assertEquals((byte) 0x90, memory.readByte(3), "Octet faible adresse");
    }

    /**
     * Teste l'assemblage de l'instruction ADD.
     * Encode : OPCODE + REG_DEST + REG_SRC_A + REG_SRC_B (4 octets)
     */
    @Test
    void testAdd() throws MemoryOutOfBoundsException {
        String programme = "ADD r2, r0, r1\n";
        assembler.assemble(programme);

        // Vérifie les octets
        assertEquals((byte) 4, memory.readByte(0), "Opcode ADD");
        assertEquals((byte) 2, memory.readByte(1), "Registre dest r2");
        assertEquals((byte) 0, memory.readByte(2), "Registre src r0");
        assertEquals((byte) 1, memory.readByte(3), "Registre src r1");
    }

    /**
     * Teste l'assemblage de l'instruction JUMP avec une adresse numérique.
     * Encode : OPCODE + ADRESSE_H + ADRESSE_L (3 octets)
     */
    @Test
    void testJump() throws MemoryOutOfBoundsException {
        String programme = "JUMP @100\n";
        assembler.assemble(programme);

        // Vérifie les octets
        assertEquals((byte) 12, memory.readByte(0), "Opcode JUMP");
        // 100 = 0x0064 → octet fort = 0x00, octet faible = 0x64
        assertEquals((byte) 0x00, memory.readByte(1), "Octet fort adresse");
        assertEquals((byte) 0x64, memory.readByte(2), "Octet faible adresse");
    }

    /**
     * Teste la gestion des labels et leur résolution.
     * Un label est déclaré avec ':' et peut être utilisé avec JUMP ou BEQ/BNE.
     */
    @Test
    void testLabel() throws MemoryOutOfBoundsException {
        String programme =
            "LOAD_CONST r0, 5\n" +
            "JUMP debut\n" +
            "LOAD_CONST r1, 10\n" +
            "debut: BREAK\n";
        
        assembler.assemble(programme);

        // L'instruction JUMP est à l'adresse 3
        // Elle devrait pointer vers "debut" qui est à l'adresse 10
        int jumpAddrH = memory.readByte(4) & 0xFF;
        int jumpAddrL = memory.readByte(5) & 0xFF;
        int jumpAddr = (jumpAddrH << 8) | jumpAddrL;

        assertEquals(10, jumpAddr, "JUMP devrait pointer vers l'adresse du label 'debut'");
    }

    /**
     * Teste le parsing des valeurs hexadécimales.
     * Format : "0xHH" où HH est un nombre en base 16.
     */
    @Test
    void testHexAddress() throws MemoryOutOfBoundsException {
        String programme = "LOAD_CONST r0, 0xFF\n";
        assembler.assemble(programme);

        // Vérifie les octets
        assertEquals((byte) 1, memory.readByte(0), "Opcode LOAD_CONST");
        assertEquals((byte) 0, memory.readByte(1), "Registre r0");
        assertEquals((byte) 0xFF, memory.readByte(2), "Valeur 0xFF (255)");
    }

    /**
     * Teste l'assemblage d'un programme complet avec plusieurs instructions.
     */
    @Test
    void testComplexProgram() throws MemoryOutOfBoundsException {
        String programme =
            "LOAD_CONST r0, 5\n" +
            "LOAD_CONST r1, 6\n" +
            "ADD r2, r0, r1\n" +
            "STORE r2, @200\n" +
            "BREAK\n";

        assembler.assemble(programme);

        // Vérifier que BREAK est à la fin
        assertEquals((byte) 0, memory.readByte(15), "BREAK à la fin du programme");
    }

}
