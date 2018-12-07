package org.nextprot.api.core.service.dbxref.resolver;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class HpaXrefURLResolver extends DefaultDbXrefURLResolver {

	private final int version;

	public HpaXrefURLResolver(int version) {

		super();

		Preconditions.checkArgument(version > 0, "HPA version number should be a positive number");

		this.version = version;
	}

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();
		String url;

        if (accession.startsWith("ENSG")) {
            if (accession.endsWith("subcellular")) {
	            url = CvDatabasePreferredLink.HPA_SUBCELL.getLink();
            }
            else {
	            url = CvDatabasePreferredLink.HPA_GENE.getLink();
            }
        }
        else {
	        url = CvDatabasePreferredLink.HPA_ANTIBODY.getLink();
        }

        if (!url.contains("%v")) {

        	throw new AssertionError("cannot replace %v by the proper version "+version+": urls for HPA lack " +
			        "the %v placeholder (url="+ url +")");
        }

		return url.replace("%v", String.valueOf(version));
	}
}