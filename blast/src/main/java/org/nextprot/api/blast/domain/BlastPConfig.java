package org.nextprot.api.blast.domain;

import java.io.Serializable;


public class BlastPConfig implements Serializable {

    private final String blastDirPath;
    private final String nextprotDatabasePath;
    private boolean isDebugMode = false;

    // scoring ...
    // output ...

    public BlastPConfig(String blastDirPath, String nextprotDatabasePath) {

        this.blastDirPath = blastDirPath;
        this.nextprotDatabasePath = nextprotDatabasePath;
    }

    public String getBlastDirPath() {
        return blastDirPath;
    }

    public String getNextprotDatabasePath() {
        return nextprotDatabasePath;
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
    }

}
