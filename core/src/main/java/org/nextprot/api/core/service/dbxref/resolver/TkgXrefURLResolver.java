package org.nextprot.api.core.service.dbxref.resolver;

import java.util.HashSet;
import java.util.Set;

class TkgXrefURLResolver extends DefaultDbXrefURLResolver {

    TkgXrefURLResolver() {

        super(new PlaceholderFactoryImpl());
    }

    private static class PlaceholderN extends Placeholder {

        public PlaceholderN() {
            super("n");
        }

        //	Db_URL: http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10%n/%s.html
        //	  Note: n% is the second digit of the cell line AC and %s is the cell line AC without the 'TKG'
        //	  Example: for "DR   TKG; TKG 0377": n%=3 s%=0377
        @Override
        public String replacePlaceholderWithAccession(String templateURL, String accession) {

            return templateURL.replaceFirst(getPlaceholderText(), String.valueOf(accession.charAt(1)));
        }
    }

    private static class PlaceholderFactoryImpl implements PlaceholderFactory {

        @Override
        public Set<Placeholder> createPlaceholders() {

            Set<Placeholder> stampResolvers = new HashSet<>(2);

            stampResolvers.add(new PlaceholderN());
            stampResolvers.add(new DefaultPlaceholderS());

            return stampResolvers;
        }
    }
}