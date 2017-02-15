package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

import java.util.HashSet;
import java.util.Set;

class UcscXrefURLResolver extends DbXrefURLBaseResolver {

    public UcscXrefURLResolver() {

        super(new ResolverFactoryImpl());
    }

    private static class StampS1Resolver extends StampBaseResolver {

        public StampS1Resolver() {
            super("s1");
        }
    }

    private static class StampS2Resolver extends StampBaseResolver {

        public StampS2Resolver() {
            super("s2");
        }

        @Override
        public String resolve(String templateURL, String accession) {

            return templateURL.replaceFirst(getStamp(), "human");
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