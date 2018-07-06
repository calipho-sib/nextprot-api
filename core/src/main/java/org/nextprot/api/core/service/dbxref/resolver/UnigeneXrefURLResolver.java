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

    private static class PlaceHolderD extends Placeholder {

        public PlaceHolderD() {
            super("d");
        }

        @Override
        public String replacePlaceholderWithAccession(String templateURL, String accession) {

            return templateURL.replaceFirst(getPlaceholderText(), "Hs");
        }
    }

    private static class PlaceHolderS extends Placeholder {

        public PlaceHolderS() {
            super("s");
        }
    }

    private static class ResolverFactoryImpl implements PlaceholderFactory {

        @Override
        public Set<Placeholder> createPlaceholders() {

            Set<Placeholder> stampResolvers = new HashSet<>(2);

            stampResolvers.add(new PlaceHolderD());
            stampResolvers.add(new PlaceHolderS());

            return stampResolvers;
        }
    }
}