package org.nextprot.api.core.utils.dbxref;

import java.util.HashSet;
import java.util.Set;

class GenevisibleXrefURLResolver extends DbXrefURLBaseResolver {

    public GenevisibleXrefURLResolver() {

        super(new ResolverFactoryImpl());
    }

    private static class StampS1Resolver extends StampBaseResolver {

        public StampS1Resolver() {
            super("s1");
        }

        @Override
        public String resolve(String templateURL, String accession) {

            return templateURL.replaceFirst(getStamp(), accession);
        }
    }

    private static class StampS2Resolver extends StampBaseResolver {

        public StampS2Resolver() {
            super("s2");
        }

        @Override
        public String resolve(String templateURL, String accession) {

            return templateURL.replaceFirst(getStamp(), "HS");
        }
    }

    private static class ResolverFactoryImpl implements StampResolverFactory {

        @Override
        public Set<StampBaseResolver> createStampResolvers() {

            Set<StampBaseResolver> stampResolvers = new HashSet<>(2);

            stampResolvers.add(new StampS1Resolver());
            stampResolvers.add(new StampS2Resolver());

            return stampResolvers;
        }
    }
}