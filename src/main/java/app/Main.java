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

        boolean continuer = true;

        while (continuer) {
            afficherMenuPrincipal();
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    ecrireProgramme();
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
                    consulterEtat();
                    break;
                case "6":
                    reinitialiserCPU();
                    break;
                case "7":
                    continuer = false;
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }

        scanner.close();
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n--- Menu principal ---");
        System.out.println("1 - Ecrire un programme en assembleur");
        System.out.println("2 - Assembler le programme");
        System.out.println("3 - Executer le programme");
        System.out.println("4 - Executer pas a pas");
        System.out.println("5 - Consulter l'etat du simulateur");
        System.out.println("6 - Reinitialiser le CPU");
        System.out.println("7 - Quitter");
        System.out.print("Votre choix : ");
    }

    private static void ecrireProgramme() {
        System.out.println("\n=== Ecrire un programme en assembleur ===\n");
        System.out.println("Instructions disponibles : load, store, add, sub, mul, div, and, or, xor, jump, beq, bne, break, data, string");
        System.out.println("\nEntrez votre programme ligne par ligne.");
        System.out.println("Laissez une ligne vide pour terminer.\n");

        programme.clear();
        while (true) {
            System.out.print("  > ");
            String ligne = scanner.nextLine();
            if (ligne.trim().isEmpty()) {
                break;
            }
            programme.add(ligne);
        }

        System.out.println(programme.size() + " ligne(s) saisie(s).");
        System.out.println("Pensez a choisir l'option 2 pour assembler le programme.");
    }

    private static void assemblerProgramme() {
        System.out.println("\n=== Assembler le programme ===\n");

        if (programme.isEmpty()) {
            System.out.println("Erreur : aucun programme n'a ete saisie.");
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
            System.out.println("Assemblage reussi.");
            System.out.println("Le programme a ete charge en memoire.");
            System.out.println("Vous pouvez maintenant l'executer (option 3 ou 4).");
        } catch (Exception e) {
            System.err.println("Erreur assemblage : " + e.getMessage());
        }
    }

    private static void executerProgramme() {
        System.out.println("\n=== Executer le programme ===\n");

        try {
            cpu.reset(); // Réinitialiser le CPU
            cpu.startExecution();
            cpu.run();
            System.out.println("Execution terminee.");
        } catch (Exception e) {
            System.err.println("Erreur execution : " + e.getMessage());
        }
    }

    private static void executerPasAPas() {
        System.out.println("\n=== Executer pas a pas ===\n");

        try {
            cpu.reset(); // Réinitialiser le CPU
            cpu.startExecution();

            while (cpu.isRunning()) {
                System.out.println("PC = " + cpu.getPC());
                System.out.print("Appuyez sur Entree pour executer la prochaine instruction (ou 'q' pour quitter) : ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("q")) {
                    break;
                }

                cpu.step();
            }

            System.out.println("Execution terminee.");
        } catch (Exception e) {
            System.err.println("Erreur execution : " + e.getMessage());
        }
    }

    private static void consulterEtat() {
        System.out.println("\n=== Consulter l'etat du simulateur ===\n");
        System.out.println("Que voulez-vous consulter ?");
        System.out.println("  a - Etat de la memoire");
        System.out.println("  b - Etat des registres");
        System.out.println("  c - Compteur de programme (PC)");
        System.out.print("Votre choix : ");

        String choix = scanner.nextLine().trim().toLowerCase();

        switch (choix) {
            case "a":
                afficherMemoire();
                break;
            case "b":
                afficherRegistres();
                break;
            case "c":
                afficherPC();
                break;
            default:
                System.out.println("Choix invalide.");
        }
    }

    private static void afficherMemoire() {
        System.out.println("\n=== Etat de la memoire ===\n");
        System.out.print("Entrez l'adresse de debut (0-65535) : ");
        int addrDebut = 0;
        int addrFin = 0;

        try {
            addrDebut = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Entrez l'adresse de fin (0-65535) : ");
            addrFin = Integer.parseInt(scanner.nextLine().trim());

            if (addrDebut < 0 || addrFin > 65535 || addrDebut > addrFin) {
                System.out.println("Adresses invalides.");
                return;
            }

            System.out.println();
            for (int i = addrDebut; i <= addrFin; i++) {
                byte valeur = memory.readByte(i);
                if (valeur != 0) {
                    System.out.printf("  mem[%d] = %d (0x%02X)\n", i, valeur & 0xFF, valeur & 0xFF);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur : entrez des nombres valides.");
        } catch (MemoryOutOfBoundsException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private static void afficherRegistres() {
        System.out.println("\n=== Etat des registres ===\n");
        System.out.println("Etat des registres :");

        try {
            RegisterFile registers = cpu.getRegisters();
            for (int i = 0; i < 16; i++) {
                byte valeur = registers.get(i);
                System.out.printf("  R%d = %d\n", i, valeur & 0xFF);
            }
        } catch (RegisterOutOfBoundsException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private static void afficherPC() {
        System.out.println("\n=== Compteur de programme (PC) ===\n");
        System.out.println("PC = " + cpu.getPC());
    }

    private static void reinitialiserCPU() {
        System.out.println("\n=== Reinitialiser le CPU ===\n");
        memory.reset();
        cpu.reset();
        System.out.println("CPU reinitialise.");
        System.out.println("La memoire et les registres ont ete effaces.");
    }
}