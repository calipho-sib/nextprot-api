package org.nextprot.api.core.utils.dbxref.resolver;

import java.util.HashSet;
import java.util.Set;

class TkgXrefURLResolver extends DefaultDbXrefURLResolver {

    TkgXrefURLResolver() {

        super(new ResolverFactoryImpl());
    }

    private static class StampNResolver extends StampBaseResolver {

        public StampNResolver() {
            super("n");
        }

        //	Db_URL: http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10%n/%s.html
        //	  Note: n% is the second digit of the cell line AC and %s is the cell line AC without the 'TKG'
        //	  Example: for "DR   TKG; TKG 0377": n%=3 s%=0377
        @Override
        public String resolve(String templateURL, String accession) {

            return templateURL.replaceFirst(getStamp(), String.valueOf(accession.charAt(1)));
        }
    }

    private static class ResolverFactoryImpl implements StampResolverFactory {

        @Override
        public Set<StampBaseResolver> createStampResolvers() {

            Set<StampBaseResolver> stampResolvers = new HashSet<>(2);

            stampResolvers.add(new StampNResolver());
            stampResolvers.add(new DefaultDbXrefURLResolver.DefaultStampSResolver());

            return stampResolvers;
        }
    }
}