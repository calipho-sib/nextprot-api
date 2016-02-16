package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

import java.util.HashSet;
import java.util.Set;

class UnigeneXrefURLResolver extends DbXrefURLBaseResolver {

    public UnigeneXrefURLResolver() {

        super(new ResolverFactoryImpl());
    }

    @Override
    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession().split("\\.")[1];
    }

    private static class StampS1Resolver extends StampBaseResolver {

        public StampS1Resolver() {
            super("s1");
        }

        @Override
        public String resolve(String templateURL, String accession) {

            return templateURL.replaceFirst(getStamp(), "Hs");
        }
    }

    private static class StampS2Resolver extends StampBaseResolver {

        public StampS2Resolver() {
            super("s2");
        }
    }

    private static class ResolverFactoryImpl implements DbXrefURLBaseResolver.StampResolverFactory {

        @Override
        public Set<StampBaseResolver> createStampResolvers() {

            Set<StampBaseResolver> stampResolvers = new HashSet<>(2);

            stampResolvers.add(new StampS1Resolver());
            stampResolvers.add(new StampS2Resolver());

            return stampResolvers;
        }
    }
}