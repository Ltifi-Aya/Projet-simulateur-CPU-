package app;

import assembler.Assembler;
import core.CPU;
import core.Memory;
import core.RegisterFile;
import exception.InvalidOpcodeException;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Point d'entrée principal du simulateur de processeur 8 bits.
 * Fournit une interface interactive pour écrire, assembler et exécuter des programmes.
 */
public class Main {

    private static Memory memory;
    private static CPU cpu;
    private static Assembler assembler;
    private static List<String> programme;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Initialisation
        memory = new Memory();
        cpu = new CPU(memory);
        assembler = new Assembler(memory);
        programme = new ArrayList<>();
        scanner = new Scanner(System.in);

        afficherEntete();

        boolean continuer = true;

        while (continuer) {
            afficherMenuPrincipal();
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    saisirProgramme();
                    break;
                case "2":
                    assemblerProgramme();
                    break;
                case "3":
                    executerProgramme();
                    break;
                case "4":
                    executerPasAPas();
                    break;
                case "5":
                    afficherEtatRegistresPC();
                    break;
                case "6":
                    inspectionMemoire();
                    break;
                case "7":
                    afficherProgrammeActuel();
                    break;
                case "8":
                    chargerExemple();
                    break;
                case "9":
                    reinitialiserComplet();
                    break;
                case "10":
                    continuer = false;
                    System.out.println("\nAu revoir !");
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }

        scanner.close();
    }

    private static void afficherEntete() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("        SIMULATEUR CPU -- \"Carte Petit Utile\"");
        System.out.println("      Projet UPSSITECH / STRI 1A - 2025/2026");
        System.out.println("=".repeat(80) + "\n");
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("[1] Saisir un programme assembleur (terminer par \"FIN\")");
        System.out.println("[2] Assembler et charger le programme en memoire");
        System.out.println("[3] Lancer l'execution jusqu'au BREAK");
        System.out.println("[4] Avancer d'une seule instruction (mode pas-a-pas)");
        System.out.println("[5] Inspecter les registres et le compteur de programme");
        System.out.println("[6] Inspecter une plage memoire");
        System.out.println("[7] Afficher le programme actuel");
        System.out.println("[8] Charger un exemple de demonstration");
        System.out.println("[9] Reinitialiser entierement le simulateur");
        System.out.println("[10] Quitter");
        System.out.println("-".repeat(80));
        System.out.print("Votre choix : ");
    }

    private static void saisirProgramme() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Saisir un programme assembleur");
        System.out.println("=".repeat(80));
        System.out.println("\nInstructions disponibles : load, store, add, sub, mul, div, and, or, xor, jump, beq, bne, break");
        System.out.println("Terminez par \"FIN\" sur une ligne vide.\n");

        programme.clear();
        while (true) {
            System.out.print("  > ");
            String ligne = scanner.nextLine();
            if (ligne.trim().equalsIgnoreCase("FIN") || ligne.trim().isEmpty()) {
                if (ligne.trim().equalsIgnoreCase("FIN")) {
                    break;
                } else if (!programme.isEmpty()) {
                    break;
                }
            } else {
                programme.add(ligne);
            }
        }

        System.out.println("\n" + programme.size() + " ligne(s) saisie(s).");
        System.out.println("Pensez a choisir l'option [2] pour assembler le programme.");
    }

    private static void assemblerProgramme() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Assembler et charger le programme en memoire");
        System.out.println("=".repeat(80));

        if (programme.isEmpty()) {
            System.out.println("\nErreur : aucun programme n'a ete saisi.");
            return;
        }

        // Réinitialiser la mémoire et le CPU
        memory.reset();
        cpu.reset();

        // Construire le programme en une seule chaîne
        StringBuilder sb = new StringBuilder();
        for (String ligne : programme) {
            sb.append(ligne).append("\n");
        }

        try {
            assembler.assemble(sb.toString());
            System.out.println("\nAssemblage reussi.");
            System.out.println("Le programme a ete charge en memoire.");
            System.out.println("Vous pouvez maintenant l'executer (option [3] ou [4]).");
        } catch (Exception e) {
            System.err.println("\nErreur assemblage : " + e.getMessage());
        }
    }

    private static void executerProgramme() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Lancer l'execution jusqu'au BREAK");
        System.out.println("=".repeat(80));

        try {
            cpu.reset();
            cpu.startExecution();
            cpu.run();
            System.out.println("\nExecution terminee.");
        } catch (Exception e) {
            System.err.println("\nErreur execution : " + e.getMessage());
        }
    }

    private static void executerPasAPas() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Avancer d'une seule instruction (mode pas-a-pas)");
        System.out.println("=".repeat(80));

        try {
            if (!cpu.isRunning()) {
                cpu.reset();
                cpu.startExecution();
            }

            int instructionIndex = 0;

            while (cpu.isRunning()) {
                System.out.println();
                System.out.println("PC = " + cpu.getPC());

                // Afficher l'instruction en cours
                if (instructionIndex < programme.size()) {
                    System.out.println("Instruction [" + (instructionIndex + 1) + "] : " + programme.get(instructionIndex));
                }

                System.out.print("Appuyez sur Entree pour executer (ou 'q' pour quitter) : ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("q")) {
                    break;
                }

                cpu.step();
                instructionIndex++;
            }

            System.out.println("\nExecution pas-a-pas terminee.");
        } catch (Exception e) {
            System.err.println("\nErreur execution : " + e.getMessage());
        }
    }

    private static void afficherEtatRegistresPC() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Inspecter les registres et le compteur de programme");
        System.out.println("=".repeat(80));

        try {
            RegisterFile registers = cpu.getRegisters();
            System.out.println("\n" + "-".repeat(50));
            System.out.println("État des registres (r0 à r15) :");
            System.out.println("-".repeat(50));
            
            for (int i = 0; i < 16; i++) {
                byte valeur = registers.get(i);
                int unsignedValue = valeur & 0xFF;
                System.out.printf("  Registre r%-2d : %3d (0x%02X)\n", i, unsignedValue, unsignedValue);
            }
            
            System.out.println("-".repeat(50));
            System.out.println("Compteur de programme (PC) : " + cpu.getPC());
            System.out.println("-".repeat(50));
        } catch (RegisterOutOfBoundsException e) {
            System.err.println("\nErreur : " + e.getMessage());
        }
    }

    private static void inspectionMemoire() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Inspecter une plage memoire");
        System.out.println("=".repeat(80));

        System.out.print("\nEntrez l'adresse de debut (0-65535) : ");
        int addrDebut = 0;
        int addrFin = 0;

        try {
            addrDebut = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Entrez l'adresse de fin (0-65535) : ");
            addrFin = Integer.parseInt(scanner.nextLine().trim());

            if (addrDebut < 0 || addrFin > 65535 || addrDebut > addrFin) {
                System.out.println("\nAdresses invalides.");
                return;
            }

            System.out.println("\n" + "-".repeat(50));
            System.out.println("État de la mémoire (adresses " + addrDebut + " à " + addrFin + ") :");
            System.out.println("-".repeat(50));
            
            for (int i = addrDebut; i <= addrFin; i++) {
                byte valeur = memory.readByte(i);
                System.out.printf("  Adresse mémoire %5d : %3d (0x%02X)\n", i, valeur & 0xFF, valeur & 0xFF);
            }
            System.out.println("-".repeat(50));
        } catch (NumberFormatException e) {
            System.out.println("\nErreur : entrez des nombres valides.");
        } catch (MemoryOutOfBoundsException e) {
            System.out.println("\nErreur : " + e.getMessage());
        }
    }

    private static void afficherProgrammeActuel() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Afficher le programme actuel");
        System.out.println("=".repeat(80));

        if (programme.isEmpty()) {
            System.out.println("\nAucun programme n'a ete saisi pour le moment.");
            System.out.println("Utilisez l'option [1] pour saisir un programme.");
        } else {
            System.out.println("\nProgramme actuel :");
            for (int i = 0; i < programme.size(); i++) {
                System.out.println("  " + (i + 1) + ": " + programme.get(i));
            }
        }
    }

    private static void chargerExemple() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Charger un exemple de demonstration");
        System.out.println("=".repeat(80));

        // Réinitialiser la mémoire et le CPU pour nettoyer les anciennes données
        memory.reset();
        cpu.reset();

        programme.clear();
        programme.add("load r0,42");
        programme.add("load r1,100");
        programme.add("load r2,15");
        programme.add("store r0,500");
        programme.add("store r1,501");
        programme.add("store r2,502");
        programme.add("load_mem r3,500");
        programme.add("load_mem r4,501");
        programme.add("load_mem r5,502");
        programme.add("add r6,r3,r4");
        programme.add("sub r7,r4,r5");
        programme.add("break");

        System.out.println("\nProgramme exemple charge :");
        for (int i = 0; i < programme.size(); i++) {
            System.out.println("  " + (i + 1) + ": " + programme.get(i));
        }
        System.out.println("\nChoisissez l'option [2] pour assembler cet exemple.");
    }

    private static void reinitialiserComplet() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Reinitialiser entierement le simulateur");
        System.out.println("=".repeat(80));

        memory.reset();
        cpu.reset();
        programme.clear();

        System.out.println("\nSimulateur reinitialise :");
        System.out.println("  - Memoire effacee");
        System.out.println("  - Registres reinitialises a 0");
        System.out.println("  - Compteur de programme = 0");
        System.out.println("  - Programme en memoire efface");
    }
}