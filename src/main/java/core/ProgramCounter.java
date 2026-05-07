package core;

/**
 * Représente le compteur de programme (Program Counter) du CPU.
 * Contient l'adresse de la prochaine instruction à lire en mémoire.
 */
public class ProgramCounter {

    /** Adresse courante du compteur de programme. */
    private int address;

    /**
     * Construit un compteur de programme initialisé à 0.
     */
    public ProgramCounter() {
        this.address = 0;
    }

    /**
     * Retourne l'adresse courante du compteur.
     * @return l'adresse courante
     */
    public int get() {
        return address;
    }

    /**
     * Incrémente le compteur de 1.
     * Appelé après chaque lecture d'octet en mémoire.
     */
    public void increment() {
        address++;
    }

    /**
     * Modifie directement l'adresse du compteur.
     * Utilisé par les instructions de saut (JUMP, BEQ, BNE).
     * @param newAddress la nouvelle adresse
     */
    public void set(int newAddress) {
        this.address = newAddress;
    }

    /**
     * Remet le compteur à zéro.
     * Utilisé pour réinitialiser le CPU.
     */
    public void reset() {
        this.address = 0;
    }
}