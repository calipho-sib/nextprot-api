package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

/**
 * Format author name for publication
 *
 * Created by fnikitin on 31/08/15.
 */
public class AuthorNameFormatter {

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

        switch (suffix) {

            case "I":
            case "1st":
                return "I";
            case "2nd":
                return "II";
            case "3rd":
                return "III";
            case "4th":
                return "IV";
            case "V":
            case "5th":
                return "V";
            case "6th":
                return "VI";
            case "Filho":
            case "Jr":
                return "Jr.";
            case "Sr":
                return "Sr.";
            default:
                return suffix;
        }
    }

}
