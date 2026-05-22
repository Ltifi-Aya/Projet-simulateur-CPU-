package assembler;

import core.Memory;
import exception.MemoryOutOfBoundsException;
import instruction.Opcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assembleur — traduit un programme textuel en octets mémoire.
 *
 * Fonctionne en deux passes :
 * Passe 1 : écrit les octets en mémoire et collecte les labels
 * Passe 2 : résout les références aux labels
 */
public class Assembler {

    /** La mémoire partagée avec le CPU. */
    private final Memory memory;

    /** Adresse courante d'écriture en mémoire. */
    private int cursor;

    /**
     * Table des labels et leurs adresses.
     * Exemple : "debut" → 6
     */
    private final Map<String, Integer> labels;

    /**
     * Liste des références à résoudre en passe 2.
     * Chaque entrée = [adresse_dans_memoire, hashcode_du_label]
     */
    private final List<int[]> labelRefs;

    /**
     * Construit un assembleur avec la mémoire partagée.
     *
     * @param memory la mémoire partagée avec le CPU
     */
    public Assembler(Memory memory) {
        this.memory    = memory;
        this.cursor    = 0;
        this.labels    = new HashMap<>();
        this.labelRefs = new ArrayList<>();
    }

    /**
     * Écrit un octet en mémoire à l'adresse courante
     * et avance le curseur de 1.
     *
     * @param value l'octet à écrire
     * @throws MemoryOutOfBoundsException si le curseur dépasse 65535
     */
    private void emitByte(int value) throws MemoryOutOfBoundsException {
        // Écrit l'octet à l'adresse courante du curseur
        memory.writeByte(cursor, (byte) value);
        // Avance le curseur vers la prochaine case mémoire
        cursor++;
    }

    /**
     * Écrit une adresse 16 bits en mémoire sur 2 octets consécutifs.
     * Encodage big-endian : octet fort d'abord, octet faible ensuite.
     *
     * @param address l'adresse 16 bits à écrire (0 à 65535)
     * @throws MemoryOutOfBoundsException si le curseur dépasse 65535
     */
    private void emitWord(int address) throws MemoryOutOfBoundsException {
        // Écrit l'octet de poids fort (bits 15-8)
        emitByte((address >> 8) & 0xFF);
        // Écrit l'octet de poids faible (bits 7-0)
        emitByte(address & 0xFF);
    }

    /**
     * Extrait le numéro d'un registre depuis un token.
     * "r0" → 0, "r1" → 1, "r15" → 15
     *
     * @param token le token à parser (ex: "r0", "r15")
     * @return le numéro du registre (0 à 15)
     * @throws IllegalArgumentException si le format est invalide
     */
    private int parseRegister(String token) {
        // Vérifie que le token commence bien par 'r'
        if (!token.startsWith("r")) {
            throw new IllegalArgumentException(
                "Registre invalide : " + token +
                " — doit commencer par 'r' (ex: r0, r15)"
            );
        }
        // Extrait le numéro après le 'r'
        int index = Integer.parseInt(token.substring(1));

        // Vérifie que le numéro est dans [0, 15]
        if (index < 0 || index > 15) {
            throw new IllegalArgumentException(
                "Numéro de registre invalide : " + index +
                " — doit être dans [0, 15]"
            );
        }
        return index;
    }

    /**
     * Parse une valeur depuis un token.
     * Format 1 — décimal     : "5"    → 5
     * Format 2 — adresse     : "@100" → 100
     * Format 3 — hexadécimal : "0x64" → 100
     *
     * @param token le token à parser
     * @return la valeur entière correspondante
     */
    private int parseValue(String token) {
        // Format 2 — adresse : "@100" ou "@0x64"
        if (token.startsWith("@")) {
            return parseValue(token.substring(1));
        }
        // Format 3 — hexadécimal : "0x64"
        if (token.startsWith("0x") || token.startsWith("0X")) {
            return Integer.parseInt(token.substring(2), 16);
        }
        // Format 1 — décimal : "5", "100"
        return Integer.parseInt(token);
    }

    /**
     * Vérifie si un token est un label.
     *
     * @param token le token à vérifier
     * @return true si c'est un label
     */
    private boolean isLabel(String token) {
        // Ce n'est pas une adresse, un registre ni un nombre
        return !token.startsWith("@")
            && !token.startsWith("r")
            && !token.startsWith("0x")
            && !Character.isDigit(token.charAt(0));
    }

    /**
     * Parse et traduit une ligne assembleur en octets mémoire.
     *
     * @param line la ligne assembleur à parser
     * @throws MemoryOutOfBoundsException si la mémoire est pleine
     */
    private void parseLine(String line) throws MemoryOutOfBoundsException {

        // Supprime les commentaires
        if (line.contains("//")) {
            line = line.substring(0, line.indexOf("//"));
        }
        // Supprime les espaces
        line = line.trim();

        // Ignore les lignes vides
        if (line.isEmpty()) return;

        // Détecte les labels (ex: "debut:")
        if (line.endsWith(":")) {
            String labelName = line.substring(0, line.length() - 1);
            labels.put(labelName, cursor);
            return;
        }

        // Découpe en tokens
        // "ADD r2, r0, r1" → ["ADD", "r2", "r0", "r1"]
        // Remplace les virgules par des espaces pour mieux parser
        String[] tokens = line.replace(",", " ").split("\\s+");
        String mnemonic = tokens[0].toUpperCase();

        switch (mnemonic) {

            case "BREAK":
                // 1 octet : opcode seulement
                emitByte(Opcode.BREAK.getValue());
                break;

            case "LOAD_CONST":
            case "LOAD":
                // Peut être LOAD_CONST, LOAD_MEM ou LOAD_INDEX
                if (tokens.length == 3) {
                    // LOAD r0, 5 → LOAD_CONST
                    // ou LOAD r0, @100 → LOAD_MEM
                    String arg = tokens[2];
                    if (arg.startsWith("@")) {
                        // LOAD_MEM : 4 octets
                        emitByte(Opcode.LOAD_MEM.getValue());
                        emitByte(parseRegister(tokens[1]));
                        emitWord(parseValue(arg));
                    } else {
                        // LOAD_CONST : 3 octets
                        emitByte(Opcode.LOAD_CONST.getValue());
                        emitByte(parseRegister(tokens[1]));
                        emitByte(parseValue(arg));
                    }
                } else if (tokens.length == 4) {
                    // LOAD_INDEX : LOAD r0, @100, r1 → 5 octets
                    emitByte(Opcode.LOAD_INDEX.getValue());
                    emitByte(parseRegister(tokens[1]));
                    emitWord(parseValue(tokens[2]));
                    emitByte(parseRegister(tokens[3]));
                } else {
                    throw new IllegalArgumentException(
                        "LOAD : nombre d'arguments incorrect " +
                        "(attendu: LOAD r0, valeur ou LOAD r0, @addr ou LOAD r0, @addr, r1)"
                    );
                }
                break;

            case "LOAD_MEM":
                // 4 octets : opcode + registre + adresse(2 octets)
                emitByte(Opcode.LOAD_MEM.getValue());
                emitByte(parseRegister(tokens[1]));
                emitWord(parseValue(tokens[2]));
                break;

            case "STORE":
                // Peut être STORE ou STORE_INDEX
                if (tokens.length == 3) {
                    // STORE r0, @100 → 4 octets
                    emitByte(Opcode.STORE.getValue());
                    emitByte(parseRegister(tokens[1]));
                    emitWord(parseValue(tokens[2]));
                } else if (tokens.length == 4) {
                    // STORE_INDEX : STORE r0, @100, r1 → 5 octets
                    emitByte(Opcode.STORE_INDEX.getValue());
                    emitByte(parseRegister(tokens[1]));
                    emitWord(parseValue(tokens[2]));
                    emitByte(parseRegister(tokens[3]));
                } else {
                    throw new IllegalArgumentException(
                        "STORE : nombre d'arguments incorrect " +
                        "(attendu: STORE r0, @addr ou STORE r0, @addr, r1)"
                    );
                }
                break;

            case "ADD":
                // 4 octets : opcode + dest + srcA + srcB
                emitByte(Opcode.ADD.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                break;

            case "SUB":
                // 4 octets : opcode + dest + srcA + srcB
                emitByte(Opcode.SUB.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                break;

            case "MUL":
                // 5 octets : opcode + dest1 + dest2 + srcA + srcB
                emitByte(Opcode.MUL.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                emitByte(parseRegister(tokens[4]));
                break;

            case "DIV":
                // 5 octets : opcode + dest1 + dest2 + srcA + srcB
                emitByte(Opcode.DIV.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                emitByte(parseRegister(tokens[4]));
                break;

            case "AND":
                // 4 octets : opcode + dest + srcA + srcB
                emitByte(Opcode.AND.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                break;

            case "OR":
                // 4 octets : opcode + dest + srcA + srcB
                emitByte(Opcode.OR.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                break;

            case "XOR":
                // 4 octets : opcode + dest + srcA + srcB
                emitByte(Opcode.XOR.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                break;

            case "JUMP":
                // 3 octets : opcode + adresse(2 octets)
                emitByte(Opcode.JUMP.getValue());
                if (isLabel(tokens[1])) {
                    labelRefs.add(new int[]{cursor, tokens[1].hashCode()});
                    emitWord(0);
                } else {
                    emitWord(parseValue(tokens[1]));
                }
                break;

            case "BEQ":
                // 5 octets : opcode + regA + regB + adresse(2 octets)
                emitByte(Opcode.BEQ.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                if (isLabel(tokens[3])) {
                    labelRefs.add(new int[]{cursor, tokens[3].hashCode()});
                    emitWord(0);
                } else {
                    emitWord(parseValue(tokens[3]));
                }
                break;

            case "BNE":
                // 5 octets : opcode + regA + regB + adresse(2 octets)
                emitByte(Opcode.BNE.getValue());
                emitByte(parseRegister(tokens[1]));
                emitByte(parseRegister(tokens[2]));
                if (isLabel(tokens[3])) {
                    labelRefs.add(new int[]{cursor, tokens[3].hashCode()});
                    emitWord(0);
                } else {
                    emitWord(parseValue(tokens[3]));
                }
                break;

            case "LOAD_INDEX":
                // 5 octets : opcode + registre dest + adresse(2 octets) + registre index
                emitByte(Opcode.LOAD_INDEX.getValue());
                emitByte(parseRegister(tokens[1]));
                emitWord(parseValue(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                break;

            case "STORE_INDEX":
                // 5 octets : opcode + registre source + adresse(2 octets) + registre index
                emitByte(Opcode.STORE_INDEX.getValue());
                emitByte(parseRegister(tokens[1]));
                emitWord(parseValue(tokens[2]));
                emitByte(parseRegister(tokens[3]));
                break;

            case "DATA":
                // Directive : écrit des octets directement en mémoire
                // Exemple : DATA 10, 20, 30
                for (int i = 1; i < tokens.length; i++) {
                    int value = parseValue(tokens[i]);
                    // Vérifie que la valeur tient sur 8 bits
                    if (value < 0 || value > 255) {
                        throw new IllegalArgumentException(
                            "Valeur DATA invalide : " + value +
                            " — doit être entre 0 et 255"
                        );
                    }
                    emitByte(value);
                }
                break;

            case "STRING":
                // Directive : écrit une chaîne en mémoire (UTF-8)
                // Exemple : STRING "hello"
                if (tokens.length < 2) {
                    throw new IllegalArgumentException(
                        "STRING : spécifiez une chaîne (ex: STRING \"hello\")"
                    );
                }
                // Reconstruit la chaîne en cas d'espaces
                String str = line.substring(line.indexOf("\"") + 1);
                if (str.contains("\"")) {
                    str = str.substring(0, str.indexOf("\""));
                }
                // Écrit chaque caractère en UTF-8
                for (byte b : str.getBytes(java.nio.charset.StandardCharsets.UTF_8)) {
                    emitByte(b & 0xFF);
                }
                break;

            default:
                throw new IllegalArgumentException(
                    "Mnémonique inconnu : " + mnemonic
                );
        }
    }

    /**
     * Retrouve le nom d'un label depuis son hashcode.
     *
     * @param hash le hashcode du nom du label
     * @return le nom du label
     */
    private String findLabelByHash(int hash) {
        // Parcourt tous les labels pour trouver celui qui correspond
        for (String name : labels.keySet()) {
            if (name.hashCode() == hash) {
                return name;
            }
        }
        return null;
    }

    /**
     * Passe 2 — résout les références aux labels.
     * Remplace les placeholders 0000 par les vraies adresses.
     *
     * @throws MemoryOutOfBoundsException si une adresse est invalide
     */
    private void resolveLabels() throws MemoryOutOfBoundsException {
        // Parcourt toutes les références à résoudre
        for (int[] ref : labelRefs) {
            int addressInMemory = ref[0];
            String labelName = findLabelByHash(ref[1]);

            // Vérifie que le label existe bien
            if (labelName == null || !labels.containsKey(labelName)) {
                throw new IllegalArgumentException(
                    "Label inconnu"
                );
            }

            // Récupère l'adresse réelle du label
            int labelAddress = labels.get(labelName);

            // Remplace le placeholder par la vraie adresse
            memory.writeByte(addressInMemory,
                (byte) ((labelAddress >> 8) & 0xFF));
            memory.writeByte(addressInMemory + 1,
                (byte) (labelAddress & 0xFF));
        }
    }

    /**
     * Point d'entrée principal de l'assembleur.
     * Traduit un programme textuel en octets mémoire.
     *
     * Passe 1 → parcourt chaque ligne et écrit les octets
     * Passe 2 → résout les références aux labels
     *
     * @param programme le code assembleur à traduire
     * @throws MemoryOutOfBoundsException si la mémoire est pleine
     */
    public void assemble(String programme)
            throws MemoryOutOfBoundsException {

        // Remet le curseur à 0 pour recommencer
        cursor = 0;
        labels.clear();
        labelRefs.clear();

        // Passe 1 — parcourt chaque ligne
        String[] lines = programme.split("\n");
        for (String line : lines) {
            parseLine(line);
        }

        // Passe 2 — résout les labels
        resolveLabels();
    }

} // fin de la classe Assembler