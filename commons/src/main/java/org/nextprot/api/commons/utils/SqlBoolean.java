package org.nextprot.api.commons.utils;

/**
 * @author fnikitin
 */
public class SqlBoolean {

    public static boolean valueOf(char ch) {

        if (ch == 'Y' | ch == 'T') return true;
        return false;
    }

    public static char toChar(boolean b) {

        if (b) return 'Y';
        return 'N';
    }
}
