package core;

import exception.InvalidOpcodeException;
import exception.MemoryOutOfBoundsException;
import exception.RegisterOutOfBoundsException;
import instruction.*;

/**
 * Implémente la boucle Fetch / Decode / Execute
 */
public class CPU {

    /** La mémoire partagée avec l'assembleur. */
    private final Memory memory;

    /** Le banc de 16 registres 8 bits. */
    private final RegisterFile registers;

    /** L'unité arithmétique et logique. */
    private final ALU alu;

    /** Le compteur de programme. */
    private final ProgramCounter pc;

    /** Indique si le CPU est en cours d'exécution. */
    private boolean running;

    /**
     * Construit un CPU avec la mémoire partagée.
     * La même instance de Memory est utilisée par
     * l'assembleur ET le CPU.
     *
     * @param memory la mémoire partagée
     */
    public CPU(Memory memory) {
        this.memory    = memory;
        this.registers = new RegisterFile();
        this.alu       = new ALU();
        this.pc        = new ProgramCounter();
        this.running   = false;
    }

    /**
     * FETCH — lit l'octet à l'adresse courante du PC
     * et incrémente le PC.
     *
     * C'est la première phase du cycle Fetch/Decode/Execute.
     * Chaque appel à fetch() avance le PC de 1.
     *
     * @return l'octet lu en mémoire
     * @throws MemoryOutOfBoundsException si le PC est hors limites
     */
    private int fetch() throws MemoryOutOfBoundsException {
        // Lit l'octet à l'adresse courante du PC
        int value = memory.readByte(pc.get()) & 0xFF;
        // Avance le PC vers l'octet suivant
        pc.increment();
        return value;
    }

    /**
     * Exécute une seule instruction (Fetch/Decode/Execute).
     * Utile pour le mode pas à pas.
     *
     * @throws InvalidOpcodeException       si un opcode inconnu est rencontré
     * @throws MemoryOutOfBoundsException   si une adresse invalide est accédée
     * @throws RegisterOutOfBoundsException si un registre invalide est accédé
     */
    public void step() throws InvalidOpcodeException,
            MemoryOutOfBoundsException, RegisterOutOfBoundsException {

        if (!running) {
            return; // Ne rien faire si le CPU n'est pas en cours d'exécution
        }

        // ── 1. FETCH ──────────────────────────────
        // Lit l'opcode à l'adresse courante du PC
        int opcodeValue = fetch();

        // ── 2. DECODE ─────────────────────────────
        // Identifie l'instruction correspondante
        instruction.Opcode opcode =
            instruction.Opcode.fromValue(opcodeValue);

        // ── 3. EXECUTE ────────────────────────────
        // Exécute l'action selon l'opcode
        switch (opcode) {

            case BREAK:
                // Arrête la boucle principale
                running = false;
                break;

            case LOAD_CONST:
                // Lit le registre destination et la valeur constante
                int regDest = fetch();
                byte value  = (byte) fetch();
                new LoadConstInstruction(regDest, value)
                    .execute(registers, memory, alu);
                break;

            case LOAD_MEM:
                // Lit le registre destination et l'adresse mémoire
                int regDestMem = fetch();
                int addrH      = fetch();
                int addrL      = fetch();
                int addrMem    = (addrH << 8) | addrL;
                new LoadMemInstruction(regDestMem, addrMem)
                    .execute(registers, memory, alu);
                break;

            case STORE:
                // Lit le registre source et l'adresse mémoire
                int regSrc   = fetch();
                int storeH   = fetch();
                int storeL   = fetch();
                int storeAddr = (storeH << 8) | storeL;
                new StoreInstruction(regSrc, storeAddr)
                    .execute(registers, memory, alu);
                break;

            case ADD:
            case SUB:
            case AND:
            case OR:
            case XOR:
                // Lit les 3 registres : destination, source A, source B
                int dest = fetch();
                int rA   = fetch();
                int rB   = fetch();
                new AluInstruction(opcode, dest, -1, rA, rB)
                    .execute(registers, memory, alu);
                break;

            case MUL:
            case DIV:
                // Lit les 4 registres : dest1, dest2, source A, source B
                int dest1 = fetch();
                int dest2 = fetch();
                int rA2   = fetch();
                int rB2   = fetch();
                new AluInstruction(opcode, dest1, dest2, rA2, rB2)
                    .execute(registers, memory, alu);
                break;

            case JUMP:
                // Lit l'adresse de destination sur 16 bits
                int jumpH    = fetch();
                int jumpL    = fetch();
                int jumpAddr = (jumpH << 8) | jumpL;
                new JumpInstruction(jumpAddr, pc)
                    .execute(registers, memory, alu);
                break;

            case BEQ:
                // Lit regA, regB et l'adresse AVANT d'évaluer
                int beqA    = fetch();
                int beqB    = fetch();
                int beqH    = fetch();
                int beqL    = fetch();
                int beqAddr = (beqH << 8) | beqL;
                new BeqInstruction(beqA, beqB, beqAddr, pc)
                    .execute(registers, memory, alu);
                break;

            case BNE:
                // Lit regA, regB et l'adresse AVANT d'évaluer
                int bneA    = fetch();
                int bneB    = fetch();
                int bneH    = fetch();
                int bneL    = fetch();
                int bneAddr = (bneH << 8) | bneL;
                new BneInstruction(bneA, bneB, bneAddr, pc)
                    .execute(registers, memory, alu);
                break;

            default:
                throw new InvalidOpcodeException(opcodeValue);
        }
    }

    /**
     * Lance l'exécution du programme chargé en mémoire.
     * Boucle Fetch / Decode / Execute jusqu'au BREAK.
     *
     * @throws InvalidOpcodeException       si un opcode inconnu est rencontré
     * @throws MemoryOutOfBoundsException   si une adresse invalide est accédée
     * @throws RegisterOutOfBoundsException si un registre invalide est accédé
     */
    public void run() throws InvalidOpcodeException,
            MemoryOutOfBoundsException, RegisterOutOfBoundsException {

        // Le CPU démarre
        running = true;

        // Boucle principale — tourne jusqu'au BREAK
        while (running) {
            step();
        }
    }

    /**
     * Arrête le CPU.
     * Appelé par l'instruction BREAK.
     */
    public void stop() {
        running = false;
    }

    /**
     * Retourne la valeur d'un registre.
     * Utilisé pour afficher l'état final dans Main.
     *
     * @param index numéro du registre (0 à 15)
     * @return la valeur du registre
     * @throws RegisterOutOfBoundsException si l'index est invalide
     */
    public byte getRegister(int index)
            throws RegisterOutOfBoundsException {
        return registers.get(index);
    }

    /**
     * Retourne la valeur actuelle du compteur de programme.
     * Utilisé pour afficher l'état final dans Main.
     *
     * @return l'adresse courante du PC
     */
    public int getPC() {
        return pc.get();
    }

    /**
     * Indique si le CPU est en cours d'exécution.
     *
     * @return true si le CPU tourne, false sinon
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Remet le CPU dans son état initial.
     * Remet le PC à 0, vide les registres.
     * Utile pour les tests JUnit.
     */
    public void reset() {
        pc.reset();
        registers.reset();
        running = false;
    }

    /**
     * Initialise le CPU pour démarrer l'exécution.
     * À appeler avant de commencer l'exécution d'un programme.
     */
    public void startExecution() {
        running = true;
    }

    /**
     * Retourne le RegisterFile pour accéder à tous les registres.
     *
     * @return le RegisterFile du CPU
     */
    public RegisterFile getRegisters() {
        return registers;
    }

    /**
     * Retourne la mémoire associée à ce CPU.
     *
     * @return la mémoire
     */
    public Memory getMemory() {
        return memory;
    }

} // fin de la classe CPU