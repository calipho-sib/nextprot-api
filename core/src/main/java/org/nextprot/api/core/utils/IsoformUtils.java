package org.nextprot.api.core.utils;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Utils about isoforms
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class IsoformUtils {

	/**
	 * Gets the isoform by its name
	 * @param isoforms
	 * @param isoformName
	 * @return
     * TODO: check if redundant with getIsoformByName
	 */
	public static Isoform getIsoformByIsoName(List<Isoform> isoforms, String isoformName) {
		//TODO the isoforms should be stored in a map at the level of the Entry
		for(Isoform iso : isoforms){
			if(iso.getIsoformAccession().replaceAll("NX_", "").equals(isoformName.replaceAll("NX_", "")))
				return iso;	
		}
		return null;
	}

    /**
     * Get the canonical isoform of the given entry
     * @param entry the entry to fetch canonical isoform
     * @return the canonical isoform
     * @throws NextProtException if canonical isoform is missing
     */
    public static Isoform getCanonicalIsoform(Entry entry) {

        for (Isoform isoform : entry.getIsoforms()) {

            if (isoform.isCanonicalIsoform())
                return isoform;
        }
        throw new NextProtException(entry.getUniqueName()+" lacks canonical isoform");
    }

    /**
     * Get all isoforms except the given one
     */
    public static List<Isoform> getOtherIsoforms(Entry entry, String isoformUniqueName) {

        return entry.getIsoforms().stream()
                .filter(iso -> !iso.getIsoformAccession().equals(isoformUniqueName))
                .collect(Collectors.toList());
    }

    public static Isoform getIsoformByNameOrCanonical(Entry entry, String isoformName) {

        return  (isoformName != null) ?
                IsoformUtils.getIsoformByName(entry, isoformName) :
                IsoformUtils.getCanonicalIsoform(entry);
    }

    public static Isoform getIsoformByName(Entry entry, String name) {
        return getIsoformByName(entry.getIsoforms(), name);
    }

    public static Isoform getIsoformByName(List<Isoform> isoforms, String name) {

        if (name == null) {
            return null;
        }
        for (Isoform iso: isoforms) {
            if (name.equals(iso.getIsoformAccession())) {
                return iso;
            }
            EntityName mainEname = iso.getMainEntityName();
            if (mainEname != null && name.equalsIgnoreCase(mainEname.getName())) {
                return iso;
            }
            for (EntityName syn: iso.getSynonyms()) {
                if (name.equalsIgnoreCase(syn.getName())) {
                    return iso;
                }
            }
        }
        return null;
    }

    public static String findEntryAccessionFromIsoformAccession(String isoformAccession) {

        if (!isoformAccession.contains("-")) {
            return null;
        }

        return isoformAccession.split("-")[0];
    }

	/**
	 * Compare isoform names by lexicographic order of master accession then numerically by isoform number.
	 */
	public static class ByIsoformUniqueNameComparator implements Comparator<String> {

		@Override
		public int compare(String uniqueName1, String uniqueName2) {

			if (uniqueName1.contains("-") && uniqueName2.contains("-")) {

				String[] accessionAndNumber1 = uniqueName1.split("-");
				String[] accessionAndNumber2 = uniqueName2.split("-");

				// compare lexicographically by master accessions
				int comp = accessionAndNumber1[0].compareTo(accessionAndNumber2[0]);

				// if equals -> compare numerically by isoform number
				if (comp == 0) {

					int isoNumber1 = Integer.parseInt(accessionAndNumber1[1]);
					int isoNumber2 = Integer.parseInt(accessionAndNumber2[1]);

					comp = isoNumber1 - isoNumber2;
				}

				return comp;
			}

			return uniqueName1.compareTo(uniqueName2);
		}
	}

    /**
     * Comparison done as follow :
     * The first isoform is always the canonical one, the remaining are sorted according to main entity names with the following criteria:
     * 1. if number prefix found -> numerically compared
     * 2. then lexicographically
     * 3. if number suffix found -> numerically compared
     **/
	public static class IsoformComparator implements Comparator<Isoform> {

        private static final Pattern numPat = Pattern.compile("\\d+");
        private static final Pattern prefixNumPat = Pattern.compile("^(\\d+)[a-zA-Z\\s]+$");
        private static final Pattern suffixNumPat = Pattern.compile("^([a-zA-Z\\s]+)(\\d+)?$");

		@Override
		public int compare(Isoform iso1, Isoform iso2) {

			// 1st criterium: canonical isoform comes first
			if (iso1.isCanonicalIsoform()) { return -1; }
			if (iso2.isCanonicalIsoform()) { return 1; }

			String name1 = iso1.getMainEntityName().getName();
			String name2 = iso2.getMainEntityName().getName();

			if (numPat.matcher(name1).find() || numPat.matcher(name2).find()) {

                // compare prefixes first
                int comp = comparePrefixNumbers(name1, name2);

                // if same prefixes or no prefixes
                if (comp == 0) {

                    comp = compareStemThenSuffixNumbers(name1, name2);
                }

                return comp;
			}
            else { return name1.compareTo(name2); }
		}

		private int comparePrefixNumbers(String name1, String name2) {

			Matcher preMatcher1 = prefixNumPat.matcher(name1);
			Matcher preMatcher2 = prefixNumPat.matcher(name2);

            boolean isName1HasPrefixNumber = preMatcher1.find();
            boolean isName2HasPrefixNumber = preMatcher2.find();

            if (isName1HasPrefixNumber && isName2HasPrefixNumber) {

                int num1 = Integer.parseInt(preMatcher1.group(1));
                int num2 = Integer.parseInt(preMatcher2.group(1));

                return num1 - num2;
            }

            // name 1 comes first
            else if (isName1HasPrefixNumber) {

                return -1;
            }

            // name 2 comes first
            else if (isName2HasPrefixNumber) {

                return 1;
            }

            // no prefix number found -> compare stems
            return 0;
		}

        private int compareStemThenSuffixNumbers(String name1, String name2) {

            Matcher suffMatcher1 = suffixNumPat.matcher(name1);
            Matcher suffMatcher2 = suffixNumPat.matcher(name2);

            if (suffMatcher1.find() && suffMatcher2.find()) {

                String stem1 = suffMatcher1.group(1);
                String stem2 = suffMatcher2.group(1);

                int comp = stem1.compareTo(stem2);

                // same stem compare suffix numbers
                if (comp == 0) {

                    if (suffMatcher1.group(2) != null && suffMatcher2.group(2) != null) {

                        int num1 = Integer.parseInt(suffMatcher1.group(2));
                        int num2 = Integer.parseInt(suffMatcher2.group(2));

                        comp = num1 - num2;
                    }
                    else if (suffMatcher1.group(2) != null) {

                        comp = 1;
                    }
                    else {

                        comp = -1;
                    }
                }

                return comp;
            }

            return name1.compareTo(name2);
        }
	}
}
