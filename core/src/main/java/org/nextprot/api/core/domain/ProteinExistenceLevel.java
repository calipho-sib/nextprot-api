package org.nextprot.api.core.domain;

public enum ProteinExistenceLevel {

    PROTEIN_LEVEL(),
    TRANSCRIPT_LEVEL(),
    HOMOLOGY(),
    PREDICTED(),
    UNCERTAIN()
    ;

    private final String name;
    private final int level;

    ProteinExistenceLevel() {
        this.name = this.name().toLowerCase().replace("_", " ");
        this.level = this.ordinal()+1;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public static ProteinExistenceLevel valueOfLevel(int level) {

        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Invalid ProteinExistenceLevel: "+level);
        }
        return ProteinExistenceLevel.values()[level-1];
    }

    public static ProteinExistenceLevel valueOfString(String value) {

        switch (value) {
            case "protein level":
                return PROTEIN_LEVEL;
            case "transcript level":
                return TRANSCRIPT_LEVEL;
            case "homology":
                return HOMOLOGY;
            case "predicted":
                return PREDICTED;
            case "uncertain":
                return UNCERTAIN;
            default:
                throw new IllegalArgumentException("Invalid value "+value);
        }
    }
}
