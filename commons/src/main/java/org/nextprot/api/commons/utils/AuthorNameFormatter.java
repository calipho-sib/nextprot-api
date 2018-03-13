package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * Format author name for publication
 *
 * Created by fnikitin on 31/08/15.
 */
public class AuthorNameFormatter {

    private final Map<String, String> suffixMapping;

    public AuthorNameFormatter() {

        suffixMapping = buildSuffixMapping();
    }

    public String formatForenameInitials(String forename) {

        return formatForenameInitials(forename, null);
    }

    public String formatForenameInitials(String forename, String suffix) {

        Preconditions.checkNotNull(forename);

        if ("-".equals(forename)) return "-";
        
        StringBuilder sb = new StringBuilder();

        // split names
        for (String name : forename.split("\\s")) {

            // composed name
            if (name.contains("-")) {

                formatComposedNameInitials(name, sb);
            }
            // standard name
            else if (name.length()>0) {

                formatStandardNameInitials(name, sb);
            }
        }

        if (suffix != null && !suffix.isEmpty())
            sb.append(" ").append(formatSuffix(suffix));

        return sb.toString();
    }

    private void formatComposedNameInitials(String composedName, StringBuilder sb) {

        String[] names = composedName.split("[-]");

        for (int i=0 ; i<names.length ; i++) {

            sb.append(formatInitial(names[i]));
            sb.append("-");
        }
        sb.delete(sb.length()-1, sb.length());
    }

    private void formatStandardNameInitials(String standardName, StringBuilder sb) {

        sb.append(standardName.charAt(0));
        sb.append('.');
    }

    private String formatInitial(String name) {

        if (!name.isEmpty()) {

            return name.charAt(0)+".";
        }
        return name;
    }

    public String formatSuffix(String suffix) {

        if (!suffixMapping.containsKey(suffix)) {
            return suffix;
        }

        return suffixMapping.get(suffix);
    }

    private static Map<String, String> buildSuffixMapping() {

        Map<String, String> suffixMapping = new HashMap<>();

        suffixMapping.put("1st", "I");
        suffixMapping.put("2nd", "II");
        suffixMapping.put("3rd", "III");
        suffixMapping.put("4th", "IV");
        suffixMapping.put("5th", "V");
        suffixMapping.put("6th", "VI");
        suffixMapping.put("Filho", "Jr.");
        suffixMapping.put("Jr", "Jr.");
        suffixMapping.put("Sr", "Sr.");

        return suffixMapping;
    }
}
