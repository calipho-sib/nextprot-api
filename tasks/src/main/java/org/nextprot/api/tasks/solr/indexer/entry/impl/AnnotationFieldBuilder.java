package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.diff.AnnotationFieldBuilderDiffTest;

@EntryFieldBuilder
public class AnnotationFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		List<Annotation> annots = entry.getAnnotations();
		for (Annotation currannot : annots) {

			String category = currannot.getCategory();
			if (category.equals("function")){
				addField(Fields.FUNCTION_DESC, currannot.getDescription());
			}
			// We also should exclude uninformative category 'sequence conflict'
			if(!category.equals("tissue specificity") && !category.equals("pathway")) {//These values are indexed under other fields
				String desc = currannot.getDescription();
				//if (desc != null) 	addField(Fields.ANNOTATIONS, currannot.getCategory() + ": " + desc);
				if (desc != null) {	
					if (category.equals("sequence caution")) {
						int stringpos;
						desc = desc.split(":")[1].substring(1);
						if((stringpos=desc.indexOf("Translation")) != -1) desc=desc.substring(stringpos);
					}
					addField(Fields.ANNOTATIONS, desc);
				}
				if (category.equals("go cellular component")) {System.err.println("GO: " + desc);}
				if (category.equals("mature protein")) {
					String chainid = currannot.getSynonym();
					if(chainid != null)
						if(chainid.contains("-")) addField(Fields.ANNOTATIONS,chainid); // Uniprot FT id, like PRO_0000019235
						else addField(Fields.ANNOTATIONS,AnnotationFieldBuilderDiffTest.getSortedValueFromPipeSeparatedField(desc + " | " + chainid));
				}
				
				if (category.contains("variant")) {
					    String evidxrefaccs = "";
						List<AnnotationEvidence> evidences = currannot.getEvidences();
						if(evidences != null) for (AnnotationEvidence ev : evidences) {
							if(ev.isResourceAXref()) {
								String db = ev.getResourceDb();
								if(!evidxrefaccs.isEmpty()) evidxrefaccs += " | ";
								if(db.equals("Cosmic"))	 evidxrefaccs += db.toLowerCase() + ":" + ev.getResourceAccession();
								else if(db.equals("dbSNP"))// Just to allow comparison with incoherent current solr implementation
							      evidxrefaccs += ev.getResourceAccession(); 
								else evidxrefaccs += currannot.getSynonym(); // Uniprot FT id, like VAR_056577
							}
						}
						if(!evidxrefaccs.isEmpty()) addField(Fields.ANNOTATIONS,AnnotationFieldBuilderDiffTest.getSortedValueFromPipeSeparatedField(evidxrefaccs));
						//List<AnnotationProperty> props = currannot.getVariant(). getProperties();
						//for (AnnotationProperty prop : props)
						    //System.err.println(prop.toString());
					}
				
				//else if (!category.contains("peptide")){
				//else {
					//System.err.println("No description for category: " + category);
					/*List<AnnotationProperty> props = currannot.getProperties();
					for (AnnotationProperty prop : props)
					    System.err.println(prop.toString()); */
				}
			}
		}
		


	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ANNOTATIONS, Fields.FUNCTION_DESC);
	}

}
