package org.nextprot.api.solr.indexation.impl.solrdoc;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.solr.core.impl.schema.CvSolrField;
import org.nextprot.api.solr.indexation.SolrDocumentFactory;

import java.util.Arrays;
import java.util.List;


public class SolrCvTermDocumentFactory implements SolrDocumentFactory<CvTerm> {

    @Override
	public SolrInputDocument createSolrInputDocument(CvTerm term) {

	    NPreconditions.checkNotNull(term, "unable to create a solr doc from an undefined cvterm");

	    SolrInputDocument doc = new SolrInputDocument();

	    String ontology = term.getOntology();

	    List<String> ignoredOntologies = Arrays.asList(
	    		"OrganelleCv",           // CaliphoMisc-194, ignore this ontology
			    "NextprotAnnotationCv",  // CaliphoMisc-194, ignore this ontology
			    "UniprotFamilyCv");

	    if (!ignoredOntologies.contains(ontology)) {

		    doc.addField(CvSolrField.ID.getName(), term.getId());
		    doc.addField(CvSolrField.AC.getName(), term.getAccession());
		    String filters = term.getOntologyAltname().replaceAll("[ _-]", "").toLowerCase().replaceAll("uniprot", "up").replaceAll("nextprot", "aanp");
		    doc.addField(CvSolrField.FILTERS.getName(), filters);
		    doc.addField(CvSolrField.NAME.getName(), term.getName());
		    doc.addField(CvSolrField.NAME_S.getName(), term.getName().toLowerCase());
		    doc.addField(CvSolrField.DESCRIPTION.getName(), term.getDescription());

		    List<String> synonstrings = term.getSynonyms();
		    if (synonstrings != null) {
			    int i = synonstrings.size();
			    StringBuilder sb = new StringBuilder();
			    for (String syn : synonstrings) {
				    sb.append(syn);
				    if (--i != 0) sb.append(" | ");
			    }
			    doc.addField("synonyms", sb.toString());
		    }

		    List<CvTerm.TermProperty> properties = term.getProperties();
		    if (properties != null) {
			    doc.addField("properties", TerminologyUtils.convertPropertiesToString(properties));
		    }

		    List<DbXref> xrefs = term.getXrefs();
		    if (xrefs != null) {
			    doc.addField("other_xrefs", TerminologyUtils.convertXrefsToSolrString(xrefs));
		    }
	    }
		
		return doc;
	}
}
