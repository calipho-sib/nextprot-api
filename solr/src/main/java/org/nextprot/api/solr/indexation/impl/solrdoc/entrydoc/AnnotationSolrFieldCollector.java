package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.EntryUtils;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class AnnotationSolrFieldCollector extends EntrySolrFieldCollector {

	protected Logger logger = Logger.getLogger(AnnotationSolrFieldCollector.class);

	@Autowired
	private TerminologyService terminologyService;
	
	@Override
	public void collect(Map<EntrySolrField, Object> fields, Entry entry, boolean isGold) {
		// Function with canonical first
		List<String> function_canonical = EntryUtils.getFunctionInfoWithCanonicalFirst(entry);
		for (String finfo : function_canonical) {
			addEntrySolrFieldValue(fields, EntrySolrField.FUNCTION_DESC, finfo);
			addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, finfo);
		}

		List<Annotation> annots = entry.getAnnotations();
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			AnnotationCategory apiCategory = currannot.getAPICategory();
			String quality = currannot.getQualityQualifier();

			if (apiCategory == AnnotationCategory.FUNCTION_INFO
					|| apiCategory == AnnotationCategory.EXPRESSION_PROFILE)
				// We just processed this via the EntryUtils dedicated method,
				// and tissue specificity values are indexed under other fields
				continue;

			// We also should exclude uninformative category 'sequence conflict'
			// if(!category.equals("tissue specificity")) {//These values are
			// indexed under other fields
			// if(!apiCategory.equals(AnnotationCategory.) {//These values are
			// indexed under other fields
			String desc = currannot.getDescription();
			if (apiCategory == AnnotationCategory.GLYCOSYLATION_SITE) {
				String xref = currannot.getSynonym();
				if (xref != null)
					// It is actually not a synonym but the carbohydrate id from
					// glycosuitedb !
					addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, xref);
			}

			else if (apiCategory == AnnotationCategory.DNA_BINDING_REGION)
				addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, category);
			else if (apiCategory == AnnotationCategory.VARIANT)
				// We need to index them somehow for the GOLD/SILVER tests, or
				// do we ? in creates a lot of useless 'variant null' tokens
				desc = "Variant " + desc;
			if (desc != null) { // System.err.println(category + ": " + desc);
				if (apiCategory == AnnotationCategory.SEQUENCE_CAUTION) {
					int stringpos;
					desc = desc.split(":")[1].substring(1); // The sequence
															// AAH70170 differs
															// from that shown.
															// Reason:
															// miscellaneous
															// discrepancy
					String[] desclevels = desc.split("\\.");
					String mainreason = desclevels[0];
					if ((stringpos = mainreason.indexOf(" at position")) != -1) {
						// truncate the position
						mainreason = mainreason.substring(0, stringpos);
					}
					addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, mainreason);

					if (desclevels.length > 1) {
						if (stringpos > 0) // mainreason truncated
							desc = desc.substring(desc.indexOf(".") + 2);
						else {
							stringpos = desc.indexOf(mainreason) + mainreason.length();
							desc = desc.substring(stringpos + 2);
						}
						addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, desc);
					}
				}

				if (!category.startsWith("go") && desc.length() > 1) { // go
																		// will
																		// be
																		// indexed
																		// via
																		// cvac,
																		// not
																		// description
					if (!isGold || quality.equals("GOLD")) {
						if (apiCategory == AnnotationCategory.PHENOTYPIC_VARIATION) {
							// Get BED data (also get the notes ? )
							Map<String, AnnotationIsoformSpecificity> annotSpecs = currannot.getTargetingIsoformsMap();
							for (Map.Entry<String, AnnotationIsoformSpecificity> mapentry : annotSpecs.entrySet()) {
								String subjectName = mapentry.getValue().getName();
								// update description with the subject for each
								// target isofotm
								addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, subjectName + " " + desc);
								// System.err.println("adding: " + subjectName +
								// " " + desc);
							}
						} else
							addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, desc);
					}
				}
				// in pathway and disease new annotations may appear due to
				// transformation of specific xrefs (orphanet...) into
				// annotations in the api
			}

			handleAnnotationTerm(fields, currannot, entry, isGold);
			
			if (apiCategory == AnnotationCategory.MATURE_PROTEIN
					|| apiCategory == AnnotationCategory.MATURATION_PEPTIDE) {
				StringBuilder chainid = new StringBuilder(currannot.getSynonym());
				if (chainid.length() > 0) {
					// System.err.println( currannot.getAllSynonyms().size() +
					// " synonyms: " + currannot.getAllSynonyms());
					if (chainid.toString().contains("-"))
						addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, chainid.toString()); // Uniprot FT id,
																// like
																// PRO_0000019235,
																// shouldn't be
																// called a
																// synonym
					else {
						List<String> chainsynonyms = currannot.getSynonyms();
						if (chainsynonyms.size() == 1)
							addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS,
									StringUtils.getSortedValueFromPipeSeparatedField(desc + " | " + chainid));
						else {
							// TODO: there is already a stringbuilder !!
							chainid = new StringBuilder();
							for (String syno : chainsynonyms) {
								chainid.append(syno).append(" | ");
							}
							addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, StringUtils.getSortedValueFromPipeSeparatedField(chainid.toString()));
						}
					}
				} // else System.err.println("chainid null for: " + desc);
					// chainid 's null for the main chain, this is wrong
			}

			// variant xrefs and identifiers
			if (apiCategory == AnnotationCategory.VARIANT) {
				String evidxrefaccs = "";
				List<AnnotationEvidence> evidences = currannot.getEvidences();
				if (evidences != null)
					for (AnnotationEvidence ev : evidences) {
						if (ev.isResourceAXref()) {
							String db = ev.getResourceDb();
							if (db == null)
								System.err.println("db is null for evidence in variant annot: " + desc);
							else {
								if (!evidxrefaccs.isEmpty())
									evidxrefaccs += " | ";
								if (db.equals("Cosmic"))
									evidxrefaccs += db.toLowerCase() + ":" + ev.getResourceAccession();
								else if (db.equals("dbSNP"))// Just to allow
															// comparison with
															// incoherent
															// current solr
															// implementation
									evidxrefaccs += ev.getResourceAccession();
								else
									evidxrefaccs += currannot.getSynonym(); // Uniprot
																			// FT
																			// id,
																			// like
																			// VAR_056577
							}
						}
					}
				if (!isGold || quality.equals("GOLD")) {
					if (!evidxrefaccs.isEmpty())
						addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, StringUtils.getSortedValueFromPipeSeparatedField(evidxrefaccs));
					Collection<AnnotationProperty> props = currannot.getProperties();
					for (AnnotationProperty prop : props)
						if (prop.getName().equals("mutation AA"))
							// eg: p.D1685E, it is unclear why this property
							// exists only in cosmic variants
							addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, prop.getValue());
				}
			}
		}

		// Families (why not part of Annotations ?), always GOLD
		for (Family family : entry.getOverview().getFamilies()) {
			String ac = family.getAccession();
			int stringpos = 0;
			addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, ac);
			String famdesc = family.getDescription();
			// There is no get_synonyms() method for families -> can't access
			// PERVR for FA-04785
			addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, famdesc);
			stringpos = famdesc.indexOf("elongs to ") + 14;
			famdesc = famdesc.substring(stringpos); // Skip the 'Belongs to' and
													// what may come before (eg:
													// NX_P19021)
			famdesc = famdesc.substring(0, famdesc.length() - 1); // remove
																	// final dot
			addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, famdesc);
			String[] families = famdesc.split("\\. "); // are there subfamilies
														// ?
			if (families.length > 1) { // Always GOLD
				for (int i = 0; i < families.length; i++) {
					addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, families[i]);
					if (families[i].contains(") superfamily")) { // index one
																	// more time
																	// without
																	// parenthesis
						famdesc = families[i].substring(0, families[i].indexOf("(")) + "superfamily";
						addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, famdesc);
					}
				}
			}
			// Sonetimes these synonymes are wrong eg: NX_Q6NUT3 -> Major
			// facilitator (TC 2.A.1) superfamily
			List<String> famsynonyms = terminologyService.findCvTermByAccessionOrThrowRuntimeException(ac).getSynonyms();
			if (famsynonyms != null)
				for (String famsynonym : famsynonyms)
					addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, famsynonym.trim());
		}
	}

	private void handleAnnotationTerm(Map<EntrySolrField, Object> fields, Annotation currannot, Entry entry, boolean isGold) {
		
		String quality = currannot.getQualityQualifier();
		String cvac = currannot.getCvTermAccessionCode();
		if (cvac != null && !cvac.isEmpty()) {
			if (cvac.startsWith("GO:")) {
				boolean allnegative = true;
				// We don't index negative annotations
				for (AnnotationEvidence ev : currannot.getEvidences())
					allnegative = allnegative & ev.isNegativeEvidence();
				if (allnegative) {
					return;
				}
			}
			if (!isGold || quality.equals("GOLD")) {
				addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, cvac);
				addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, currannot.getCvTermName());
				
				CvTerm term = terminologyService.findCvTermByAccession(cvac);
				if (null==term) {
					// there is nothing more we can add to indexed fields (ancestors, synonyms), so let's return
					logger.error(entry.getUniqueName() + " - term with accession |" + cvac + "| not found with findCvTermByAccession()");
					return;
				}
				
				List<String> synonyms = term.getSynonyms();
				if (synonyms != null) {
					String allsynonyms = "";
					for (String synonym : synonyms) {
						if (!allsynonyms.isEmpty())
							allsynonyms += " | ";
						allsynonyms += synonym.trim();
					}
					addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, StringUtils.getSortedValueFromPipeSeparatedField(allsynonyms));
				}

				List<String> ancestors = terminologyService.getAllAncestorsAccession(cvac);
				String allancestors = "";
				for (String ancestor : ancestors) {
					if (!allancestors.isEmpty())
						allancestors += " | ";
					allancestors += ancestor + " | "; // adding Ac
					String ancestorname = terminologyService.findCvTermByAccessionOrThrowRuntimeException(ancestor).getName();
					allancestors += ancestorname;
				}
				if (allancestors.endsWith(" domain"))
					allancestors = "domain"; // don't index generic top
												// level ancestors
				else if (allancestors.endsWith("zinc finger region"))
					allancestors = "zinc finger region"; // don't index
															// generic top
															// level
															// ancestors
				else if (allancestors.endsWith("repeat"))
					allancestors = "repeat"; // don't index generic top
												// level ancestors
				if (allancestors.length() > 1)
					addEntrySolrFieldValue(fields, EntrySolrField.ANNOTATIONS, StringUtils.getSortedValueFromPipeSeparatedField(allancestors));
			}
		}
	}

	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.ANNOTATIONS, EntrySolrField.FUNCTION_DESC);
	}
}
