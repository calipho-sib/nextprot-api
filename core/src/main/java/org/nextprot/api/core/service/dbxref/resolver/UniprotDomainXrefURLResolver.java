package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UniprotDomainXrefURLResolver extends DefaultDbXrefURLResolver {

    private static final Pattern pattern = Pattern.compile("(\\w+)\\s+((\\bdomain\\b)|(\\brepeat\\b)|(\\bzinc finger\\b))");

    @Override
    public String getTemplateURL(DbXref xref) {

        return CvDatabasePreferredLink.UNIPROT_DOMAIN.getLink();
    }

    @Override
    protected String resolveURL(String templateURL, String accession) {

        String domainAccession = accession;

        Matcher matcher = pattern.matcher(accession);

        // remove suffix from accession
        if (matcher.find()) {
            domainAccession = matcher.group(1);
        }

        return super.resolveURL(templateURL, domainAccession);
    }
}