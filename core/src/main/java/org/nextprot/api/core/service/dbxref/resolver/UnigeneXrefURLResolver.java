package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

import java.util.HashSet;
import java.util.Set;

class UnigeneXrefURLResolver extends DefaultDbXrefURLResolver {

    public UnigeneXrefURLResolver() {

        super(new ResolverFactoryImpl());
    }

    @Override
    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession().split("\\.")[1];
    }

    private static class StampDResolver extends StampBaseResolver {

        public StampDResolver() {
            super("d");
        }

        @Override
        public String resolve(String templateURL, String accession) {

            return templateURL.replaceFirst(getStamp(), "Hs");
        }
    }

    private static class StampSResolver extends StampBaseResolver {

        public StampSResolver() {
            super("s");
        }
    }

    private static class ResolverFactoryImpl implements DefaultDbXrefURLResolver.StampResolverFactory {

        @Override
        public Set<StampBaseResolver> createStampResolvers() {

            Set<StampBaseResolver> stampResolvers = new HashSet<>(2);

            stampResolvers.add(new StampDResolver());
            stampResolvers.add(new StampSResolver());

            return stampResolvers;
        }
    }
}