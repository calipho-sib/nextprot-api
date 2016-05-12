package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
//import java.util.Set;
//import java.util.SortedSet;
//import java.util.TreeSet;




import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
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
				//if (desc != null) 	addField(Fields.ANNOTATIONS, currannot.getCategory() + ": " + desc); glycosylation site
				if(category.equals("glycosylation site")) {
					String xref = currannot.getSynonym();
					if(xref != null)
						// It is actually not a synonyme but the carbohydrate id from glycosuitedb !
						addField(Fields.ANNOTATIONS, xref);
				}
				/*if(category.contains("BinaryInteraction")) {
					   Collection<AnnotationProperty> props = currannot.getProperties();
					   for(AnnotationProperty prop : props)
					  System.err.println(prop.getName() + "=" + prop.getValue());
				}*/
				if(category.equals("DNA-binding region")) addField(Fields.ANNOTATIONS, category);
				if (desc != null) {	//System.err.println(category + ": " + desc);
					if (category.equals("sequence caution")) {
						int stringpos=0;
						//System.err.println("raw: " + desc);
						desc = desc.split(":")[1].substring(1); // The sequence AAH70170 differs from that shown. Reason: miscellaneous discrepancy
						String[] desclevels = desc.split("\\.");
						String mainreason = desclevels[0];
						if((stringpos=mainreason.indexOf(" at position")) != -1) {
							// truncate the position
							mainreason=mainreason.substring(0,stringpos);
						}
						//System.err.println("mainreason: " + mainreason);
						addField(Fields.ANNOTATIONS, mainreason); 
						
						//if((stringpos=desc.indexOf(" at position")) != -1) {desc=desc.substring(0,stringpos);/*System.err.println( "desc: " + desc);*/}
						if(desclevels.length > 1) {
							if(stringpos > 0) // mainreason truncated
								desc = desc.substring(desc.indexOf(".") + 2);
							else {
							stringpos=desc.indexOf(mainreason) + mainreason.length();
						    desc = desc.substring(stringpos+2);
							}
							//System.err.println("newdesc " + desc);
							addField(Fields.ANNOTATIONS, desc);
							}
						/*if(desc.startsWith("miscellaneous")) addField(Fields.ANNOTATIONS, "miscellaneous discrepancy");
						else if(desc.startsWith("frameshift")) addField(Fields.ANNOTATIONS, "frameshift");
						if((stringpos=desc.indexOf("Translation")) != -1) desc=desc.substring(stringpos);
						else if((stringpos=desc.indexOf(". ")) != -1) desc=desc.substring(stringpos+2); */
					}
					//System.err.println(category);
					if((category.equals("sequence variant") || category.equals("mutagenesis site")) && desc.startsWith("Missing"))
						// Remove variation descriptor (Missing)
						desc = desc.substring(8); // don't index variant status
					if(!category.startsWith("go") && desc.length() > 1)
						addField(Fields.ANNOTATIONS, desc); // go will be indexed via cvac, not description
					// in pathway and disease new annotations may appear due to transformation of specific xrefs (orphanet...) into annotations in the api
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
						String allancestors="";
						for (String ancestor : ancestors) {
						if(!allancestors.isEmpty()) allancestors += " | ";
						String ancestorname = this.terminologyservice.findTerminologyByAccession(ancestor).getName();
						allancestors += ancestorname;
						//System.err.println("allancestors: " + allancestors);
				        }
					//System.err.println("allancestors len: " + allancestors.length());	
					if(allancestors.endsWith(" domain"))	allancestors="domain"; // don't index generic top level ancestors
					else if(allancestors.endsWith("zinc finger region"))	allancestors="zinc finger region"; // don't index generic top level ancestors
					else if(allancestors.endsWith("repeat"))	allancestors="repeat"; // don't index generic top level ancestors
					if(allancestors.length() > 1) //System.err.println("adding: " + allancestors);
						addField(Fields.ANNOTATIONS, StringUtils.getSortedValueFromPipeSeparatedField(allancestors));
				}
				//if (category.equals("disease")) {System.err.println("Disease: " + desc);}
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
								chainid = "";
								for (String syno : chainsynonyms) {
									chainid += syno + " | ";
								}
							  // System.err.println("chainsynonyms: " + StringUtils.getSortedValueFromPipeSeparatedField(chainid));	
							  addField(Fields.ANNOTATIONS,StringUtils.getSortedValueFromPipeSeparatedField(chainid));
							}
						}
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
						Collection<AnnotationProperty> props = currannot.getProperties();
						for (AnnotationProperty prop : props) if(prop.getName().equals("mutation AA")) {
							//System.err.println("adding: " + prop.getValue());
							addField(Fields.ANNOTATIONS,prop.getValue()); // eg: p.D1685E
						}
					}
				}
			}
		
			// Families (why not part of Annotations ?)
			for (Family family : entry.getOverview().getFamilies()) {
				String ac = family.getAccession();
				int stringpos = 0;
				addField(Fields.ANNOTATIONS, ac);
				String famdesc = family.getDescription(); 
				// There is no get_synonyms() method for families -> can't access PERVR for FA-04785
				addField(Fields.ANNOTATIONS,  famdesc);
				stringpos = famdesc.indexOf("elongs to ") + 14;
				//famdesc = famdesc.substring(stringpos,stringpos+1).toUpperCase() + famdesc.substring(stringpos+1); // Skip the 'Belongs to' and what may come before (eg: NX_P19021)
				famdesc = famdesc.substring(stringpos); // Skip the 'Belongs to' and what may come before (eg: NX_P19021)
				famdesc = famdesc.substring(0,famdesc.length()-1); // remove final dot
				addField(Fields.ANNOTATIONS,  famdesc);
				//System.err.println("famdesc2: " + famdesc);
				String[] families = famdesc.split("\\. "); // are there subfamilies ?
				if(families.length > 1) {
					for(int i=0; i< families.length; i++) {
						addField(Fields.ANNOTATIONS,  families[i]);
						if(families[i].contains(") superfamily")) { // index one more time without parenthesis
							famdesc = families[i].substring(0, families[i].indexOf("(")) + "superfamily";
							//System.err.println("famdesc: " + famdesc);
							addField(Fields.ANNOTATIONS,  famdesc);
						    }
						}
					}
				// Sonetimes these synonymes are wrong eg: NX_Q6NUT3 -> Major facilitator (TC 2.A.1) superfamily
				List<String> famsynonyms = this.terminologyservice.findTerminologyByAccession(ac).getSynonyms();
				if(famsynonyms != null) for(String famsynonym : famsynonyms)
					addField(Fields.ANNOTATIONS,  famsynonym.trim());
				}

		}
		

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ANNOTATIONS, Fields.FUNCTION_DESC);
	}

}
