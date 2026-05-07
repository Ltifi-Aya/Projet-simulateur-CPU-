package app;

import assembler.Assembler;
import core.CPU;
import core.Memory;

/**
 * Point d'entrée principal du simulateur de processeur 8 bits.
 * Orchestre l'initialisation des composants, l'assemblage
 * du programme et le lancement de l'exécution sur le CPU.
 */
public class Main {

    public static void main(String[] args) {

        // ─────────────────────────────────────────────
        // 1. Création de la mémoire partagée unique
        //    L'assembleur ET le CPU opèrent sur la
        //    même instance de Memory
        // ─────────────────────────────────────────────
        Memory memory = new Memory();

        // ─────────────────────────────────────────────
        // 2. Programme assembleur
        //    Démo : charge 5 et 6, additionne,
        //    stocke le résultat, puis arrête
        // ─────────────────────────────────────────────
        String programme =
            "LOAD_CONST r0, 5\n" +
            "LOAD_CONST r1, 6\n" +
            "ADD r2, r0, r1\n"   +
            "STORE r2, @200\n"   +
            "BREAK\n";

        // ─────────────────────────────────────────────
        // 3. Assemblage du programme en mémoire
        // ─────────────────────────────────────────────
        Assembler assembler = new Assembler(memory);
        try {
            assembler.assemble(programme);
            System.out.println("✅ Assemblage réussi !");
        } catch (Exception e) {
            System.err.println("❌ Erreur assemblage : "
                + e.getMessage());
            return;
        }

        // ─────────────────────────────────────────────
        // 4. Création et lancement du CPU
        // ─────────────────────────────────────────────
        CPU cpu = new CPU(memory);
        try {
            cpu.run();
            System.out.println("✅ Exécution terminée !");
        } catch (Exception e) {
            System.err.println("❌ Erreur exécution : "
                + e.getMessage());
            return;
        }

        // ─────────────────────────────────────────────
        // 5. Affichage des résultats finaux
        // ─────────────────────────────────────────────
        try {
            System.out.println("\n=== État final ===");
            System.out.println("r0          = "
                + (cpu.getRegister(0) & 0xFF)
                + "   (attendu : 5)");
            System.out.println("r1          = "
                + (cpu.getRegister(1) & 0xFF)
                + "   (attendu : 6)");
            System.out.println("r2          = "
                + (cpu.getRegister(2) & 0xFF)
                + "  (attendu : 11)");
            System.out.println("mémoire[200]= "
                + (memory.readByte(200) & 0xFF)
                + "  (attendu : 11)");
            System.out.println("PC final    = "
                + cpu.getPC());
        } catch (Exception e) {
            System.err.println("❌ Erreur lecture : "
                + e.getMessage());
        }
    }
}