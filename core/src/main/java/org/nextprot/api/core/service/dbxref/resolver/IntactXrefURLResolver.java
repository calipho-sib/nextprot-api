package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IntactXrefURLResolver extends DefaultDbXrefURLResolver {

    private static final Pattern PATTERN = Pattern.compile("(EBI-\\w+),(EBI-\\w+)");
    
    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        Matcher matcher = PATTERN.matcher(accession);
    
        if (matcher.find()) {
            return CvDatabasePreferredLink.INTACT_BINARY.getLink();
        }

        return super.getTemplateURL(xref);
    }
    
    @Override
    protected String resolveURL(String templateURL, String accession) {
        
        Matcher matcher = PATTERN.matcher(accession);
        
        if (matcher.find()) {
            return templateURL.replaceFirst("%s1", matcher.group(1)).replaceFirst("%s2", matcher.group(2));
        }
        return super.resolveURL(templateURL, accession);
        
        
    }
}