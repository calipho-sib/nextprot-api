package org.nextprot.api.core.utils.dbxref.resolver;

class ConstantXrefDatabasebURLResolver extends DefaultDbXrefURLResolver {

    private final String urlDatabase;

    ConstantXrefDatabasebURLResolver(String urlDatabase) {

        this.urlDatabase = urlDatabase;
    }

    @Override
    public String getValidXrefURL(String xrefURL, String databaseName) {

        return urlDatabase;
    }
}