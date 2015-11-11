package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
//import java.util.Set;
//import java.util.SortedSet;
//import java.util.TreeSet;

import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class AnnotationFieldBuilder extends FieldBuilder {
	
	private TerminologyService terminologyservice;

	@Override
	protected void init(Entry entry) {

		List<Annotation> annots = entry.getAnnotations();
		//Overview ovv = entry.getOverview();
		for (Annotation currannot : annots) {

			String category = currannot.getCategory();
			if (category.equals("function")){
				addField(Fields.FUNCTION_DESC, currannot.getDescription());
			}
			// We also should exclude uninformative category 'sequence conflict'
			if(!category.equals("tissue specificity") && !category.contains("kinetic")) {//These values are indexed under other fields
				String desc = currannot.getDescription();
				//if (desc != null) 	addField(Fields.ANNOTATIONS, currannot.getCategory() + ": " + desc);
				if (desc != null) {	//System.err.println(category + ": " + desc);
					if (category.equals("sequence caution")) {
						int stringpos;
						System.err.println("raw: " + desc);
						desc = desc.split(":")[1].substring(1); // The sequence AAH70170 differs from that shown. Reason: miscellaneous discrepancy
						String[] desclevels = desc.split("\\.");
						//System.err.println(desclevels.length + " levels");
						desc = desclevels[0];
						if((stringpos=desc.indexOf(" at position")) != -1) {desc=desc.substring(0,stringpos);System.err.println( "desc: " + desc);}
						if(desclevels.length > 1)
							for(int i=1; i< desclevels.length; i++) {
								String desc2 = desclevels[i].substring(1);
								if(i == desclevels.length-1) desc2 += ".";
								addField(Fields.ANNOTATIONS, desc2);
							}
						/*if(desc.startsWith("miscellaneous")) addField(Fields.ANNOTATIONS, "miscellaneous discrepancy");
						else if(desc.startsWith("frameshift")) addField(Fields.ANNOTATIONS, "frameshift");
						if((stringpos=desc.indexOf("Translation")) != -1) desc=desc.substring(stringpos);
						else if((stringpos=desc.indexOf(". ")) != -1) desc=desc.substring(stringpos+2); */
					}
					if((category.equals("sequence variant")) && desc.startsWith("Missing")) desc = desc.substring(8); // don't index variant status
					if(!category.startsWith("go") && desc.length() > 1) addField(Fields.ANNOTATIONS, desc); // go will be indeved via cvac, not description
					// in pathway and disease new annotations may appear due to transormation of specific xrefs (orphanet...) into annotations in the api
				}
				String cvac = currannot.getCvTermAccessionCode();
				//System.err.println(currannot.getCategory());
				if (cvac != null) { 
					addField(Fields.ANNOTATIONS, cvac);
					addField(Fields.ANNOTATIONS,  currannot.getCvTermName());
					List<String> synonyms = this.terminologyservice.findTerminologyByAccession(cvac).getSynonyms();
					if(synonyms != null) {
						String allsynonyms="";
						for (String synonym : synonyms) {
						if(!allsynonyms.isEmpty()) allsynonyms += " | ";
						allsynonyms += synonym.trim();
						}	
					addField(Fields.ANNOTATIONS,StringUtils.getSortedValueFromPipeSeparatedField(allsynonyms)); 
					}
					
					List<String> ancestors = TerminologyUtils.getAllAncestors(cvac, terminologyservice);
					//System.err.println(ancestors.size() + " ancestors");
						String allancestors="";
						for (String ancestor : ancestors) {
						if(!allancestors.isEmpty()) allancestors += " | ";
						allancestors += this.terminologyservice.findTerminologyByAccession(ancestor).getName();
				        }
					if(allancestors.endsWith("domain"))	allancestors="domain"; // don't index generic top level ancestors
					else if(allancestors.endsWith("zinc finger region"))	allancestors="zinc finger region"; // don't index generic top level ancestors
					else if(allancestors.endsWith("repeat"))	allancestors="repeat"; // don't index generic top level ancestors
					addField(Fields.ANNOTATIONS, StringUtils.getSortedValueFromPipeSeparatedField(allancestors));	
				}
				if (category.equals("disease")) {System.err.println("Disease: " + desc);}
				if (category.equals("mature protein") || category.equals("maturation peptide")) {
					String chainid = currannot.getSynonym();
					if(chainid != null) {
						//System.err.println(  currannot.getAllSynonyms().size() + " synonyms: " +  currannot.getAllSynonyms());
						if(chainid.contains("-")) addField(Fields.ANNOTATIONS,chainid); // Uniprot FT id, like PRO_0000019235, shouldn't be called a synonym
						else  {
							List<String> chainsynonyms = currannot.getSynonyms();
							if(chainsynonyms.size() == 1)
							  addField(Fields.ANNOTATIONS,StringUtils.getSortedValueFromPipeSeparatedField(desc + " | " + chainid));
							else {
							  chainid = chainsynonyms.toString().substring(1).replace(",", " |").replace("]", "");
							  addField(Fields.ANNOTATIONS,StringUtils.getSortedValueFromPipeSeparatedField(chainid));
							}
						}
						//List <EntityName> altnames = ovv.getProteinNames();
						//for (EntityName name : altnames) System.err.println("name: " + name.getName());
						/*System.err.println("Asking properties for: " +  category);
						List<AnnotationProperty> props = currannot.getProperties();
						System.err.println(props.size() + " properties");
						for (AnnotationProperty prop : props) System.err.println("prop: " + prop.getName());*/

					} // else System.err.println("chainid null for: " + desc); chainid 's null for the main chain, this is wrong
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
								//if(currannot.getSynonym().length() == 0) System.err.println(currannot.getCategory() + " : empty desc");
							}
						}
						if(!evidxrefaccs.isEmpty()) addField(Fields.ANNOTATIONS,StringUtils.getSortedValueFromPipeSeparatedField(evidxrefaccs));
						List<AnnotationProperty> props = currannot.getProperties();
						for (AnnotationProperty prop : props) if(prop.getName().equals("mutation AA")) {
							//System.err.println("adding: " + prop.getValue());
							addField(Fields.ANNOTATIONS,prop.getValue()); // eg: p.D1685E
						}
					}
				//if(category.equals("pathway")) {//These values are indexed under other fields
				
				}
			}
		
			// Families (why not part of Annotations ?)
			for (Family family : entry.getOverview().getFamilies()) { 
				addField(Fields.ANNOTATIONS, family.getAccession());
				String famdesc = family.getDescription(); 
				addField(Fields.ANNOTATIONS,  famdesc);
				famdesc = famdesc.substring(15,16).toUpperCase() + famdesc.substring(16); // Skip the 'Belongs to'
				famdesc = famdesc.substring(0,famdesc.length()-1); // remove final dot
				addField(Fields.ANNOTATIONS,  famdesc);
				String[] families = famdesc.split("\\. "); // are there subfamilies ?
				if(families.length > 1) {
					for(int i=0; i< families.length; i++) {
						addField(Fields.ANNOTATIONS,  families[i]);
					System.err.println("adding: " + families[i]); }
					}
				}

		}
		

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ANNOTATIONS, Fields.FUNCTION_DESC);
	}

	public void setTerminologyservice(TerminologyService terminologyservice) {
		this.terminologyservice = terminologyservice;
	}
}
