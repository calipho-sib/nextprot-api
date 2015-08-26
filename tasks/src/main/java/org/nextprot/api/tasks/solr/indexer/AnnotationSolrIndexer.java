package org.nextprot.api.tasks.solr.indexer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.solr.common.SolrInputDocument;
//import org.biojavax.bio.seq.io.UniProtCommentParser.Interaction;
//import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.core.domain.Interactant;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.EntityName;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.DbXrefService;
//import org.nextprot.api.core.service.TerminologyService;
//import org.nextprot.api.import org.nextprot.api.core.domain.AntibodyMapping;
//import org.nextprot.api.core.utils.TerminologyUtils;
//import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;
import org.reflections.Reflections;


public class AnnotationSolrIndexer extends SolrIndexer<Entry> {
	
	private TerminologyService terminologyservice;
	private DbXrefService dbxrefservice;

	public AnnotationSolrIndexer(String url) {
		super(url);
	}

	@Override
	public SolrInputDocument convertToSolrDocument(Entry entry) {
		SolrInputDocument doc = new SolrInputDocument();
		Set <String> cv_acs = new HashSet<String>();
		Set <String> cv_ancestors_acs = new HashSet<String>();
		Set <String> cv_synonyms = new HashSet<String>();
		Set <String> cv_tissues = new HashSet<String>();
		String id = entry.getUniqueName();
		//Integer pe_level = 0;
		//
		doc.addField("id", id);
		doc.addField("idsp0", id);
		doc.addField("recommended_ac", id.substring(3));
		Overview ovv = entry.getOverview(); 
		doc.addField("protein_existence", ovv.getProteinExistence());
		int pe_level = ovv.getProteinExistenceLevel(); // Will be used to compute informational score
		doc.addField("pe_level", pe_level);
		//doc.addField("isoform_num", entry.getIsoforms().size());
		String precname = ovv.getMainProteinName();
		System.err.println(id + " " + precname);
		doc.addField("recommended_name", precname);
		doc.addField("recommended_name_s", precname);

		// Filters and entry properties
		EntryProperties props = entry.getProperties();
		doc.addField("isoform_num", props.getIsoformCount());
		int cnt;
		cnt = props.getPtmCount();
		if(cnt > 0) doc.addField("ptm_num", cnt);
		cnt = props.getVarCount();
		if(cnt > 0) doc.addField("var_num", cnt);
		String filters = "";
		if(props.getFilterstructure()) filters += "filterstructure ";
		if(props.getFilterdisease()) filters += "filterdisease ";
		if(props.getFilterexpressionprofile()) filters += "filterexpressionprofile ";
		if(props.getFiltermutagenesis()) filters += "filtermutagenesis ";
		if(props.getFilterproteomics()) filters += "filterproteomics ";
		if(filters.length() > 0) doc.addField("filters", filters);
		doc.addField("aa_length", props.getMaxSeqLen()); // max length among all isoforms

		
		List <EntityName> altnames = null;
		altnames = ovv.getProteinNames();
		if(altnames != null )
			for (EntityName altname : altnames) {
				List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				for (EntityName paltfullname : paltnames) {
				    doc.addField("alternative_names", paltfullname.getName());
				    List <EntityName> paltshortnames = paltfullname.getSynonyms();
				    if(paltshortnames != null )
				    for (EntityName paltshortname : paltshortnames) {
				    	doc.addField("alternative_names", paltshortname.getName());
				    }
				}
			}
		
		altnames = ovv.getAdditionalNames(); // special names (INN, allergens)
		if(altnames != null )
			for (EntityName altname : altnames) {
				doc.addField("alternative_names", altname.getName());
			}
		
		altnames = ovv.getFunctionalRegionNames(); // The enzymatic activities of a multifunctional enzyme (maybe redundent with getEnzymes)
		if(altnames != null )
			for (EntityName altname : altnames) {
				doc.addField("region_name", altname.getName()); // region_name should be renamed activity_name
				// Synonyms allready collected in the getEnzymes loop
				/* List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				for (EntityName ecname : paltnames) {
				    //doc.addField("ec_name", ecname.getName());
					System.err.println(id + " fromincludes: " + ecname.getName());
				} */
			}
		
		// Gene names, synonyms and orf names
		List <EntityName> genenames = ovv.getGeneNames();
		if(genenames != null ) {
			String maingenename = ovv.getMainGeneName(); // TODO: check for multigene entries
			doc.addField("recommended_gene_names", maingenename);
			doc.addField("recommended_gene_names_s", maingenename);
			for (EntityName currname : genenames) {
				List <EntityName> genesynonames = currname.getSynonyms();
				if(genesynonames != null)
				for (EntityName genesynoname : genesynonames) {
				doc.addField("alternative_gene_names", genesynoname.getName());
			    //System.err.println("syn: " + genesynoname.getName()); 
				}
			}
		}
		//else System.err.println("no gene names for: " + id);
		
		List<Family> families = ovv.getFamilies();
		String allfamilies = null;
		for (Family family : families) { // alternatively use a multivalue solr field
			if(allfamilies == null) allfamilies = family.getName();
			else allfamilies += " | " + family.getName();
			cv_acs.add(family.getAccession());
			doc.addField("cv_acs", family.getAccession());
		}
		if(allfamilies == null) {doc.addField("family_names", allfamilies); doc.addField("family_names_s", allfamilies);}
		
		
		// Identifiers
		List <Identifier> identifiers = entry.getIdentifiers();
		for (Identifier currident : identifiers) {
			String idtype = currident.getType();
			//if(currident.getDatabase() == null)
			//System.err.println("type: " + idtype + " " + currident.getName());
			if(idtype.equals("Secondary AC")) doc.addField("alternative_acs", currident.getName());
			else if (idtype.equals("IMAGE") || idtype.equals("FLJ") || idtype.equals("MGC") || idtype.equals("DKFZ") || idtype.equals("Others")) doc.addField("clone_name", currident.getName());
			else if (idtype.equals("Illumina") || idtype.equals("Affymetrix")) doc.addField("microarray_probe", currident.getName());
			else if (idtype.equals("Entry name"))  doc.addField("uniprot_name", currident.getName());
			//else System.err.println("type: " + idtype);
		}
		
		List<Terminology> enzymes = entry.getEnzymes();
		String ec_names = "";
		for (Terminology currenzyme : enzymes) {
			//TODO DANIEL cvac_cnt++;
			cv_acs.add(currenzyme.getAccession());
			doc.addField("cv_names", currenzyme.getName());
			ec_names += "EC " + currenzyme.getAccession() + ", ";
			
			List <String> synonyms = currenzyme.getSynonyms();
			if(synonyms != null)
			   for (String synonym : synonyms)  doc.addField("cv_synonyms", synonym.trim());
		}
		doc.addField("ec_name", ec_names);
		
		List<Interaction> interactions = entry.getInteractions();
		//System.err.println(interactions.size() + " interactions");
		for (Interaction currinteraction : interactions) {
			//System.err.println(currinteraction.getEvidenceXrefAC()); // EBI-372273,EBI-603319
			doc.addField("interactions", currinteraction.getEvidenceXrefAC());
			List<Interactant> interactants = currinteraction.getInteractants();
			//System.err.println(interactants.size() + " interactants");
			for (Interactant currinteractant : interactants) {
				//currinteractant.
			     //System.err.println(currinteractant.getNextprotAccession() + " " + currinteractant.getUrl());
			     List<Long> ll = Arrays.asList(currinteractant.getXrefId()); // findDbXRefByIds exists but not findDbXRefById
			     DbXref xref1 = this.dbxrefservice.findDbXRefByIds(ll).get(0);
			     List<DbXrefProperty> xrefprops =  xref1.getProperties();
			     if(xrefprops != null)
			    	for (DbXrefProperty xrefprop : xrefprops) {
			    		 System.err.println("propname: " + xrefprop.getName()); // never shows
			    	 } //else System.err.println("no properties for: " + xref1.getAccession());
			    	 //System.err.println("propval: " + xref1.getAccession());
			    	 //System.err.println("propval: " + xref1.getPropertyValue("gene designation"));
			}
			//doc.addField("interactions", interaction.getAccession());
		}

		// Xrefs
		List<DbXref> xrefs = entry.getXrefs();
		for (DbXref xref : xrefs) {
			String acc =  xref.getAccession();
			String db = xref.getDatabaseName();
			//System.err.println(db+":"+acc);
			//if(db.equals("IntAct")) System.err.println("id " +  xref.getDbXrefId() + ": " +  xref.getPropertyValue("gene designation")); 
			/*if(db.equals("neXtProt")) {
				if(acc.equals(id)) continue; // Internal stuff like NX_VG_10_51732257_248
				String gen = xref.getPropertyValue("gene designation");
				System.err.println("nonxeno: " + gen);
			}*/
			if((db.equals("UniProt") || db.equals("neXtProt")) && !id.contains(acc)) { // wrong for nextprot gene designation -> protein name
				String gen = xref.getPropertyValue("gene designation");
				if(gen != null && gen != "-") { gen = gen.toUpperCase(); System.err.println(acc + ": " + gen); doc.addField("interactions", gen);}
				//else System.err.println("no gene for: " + acc );
				} 
			if(db.equals("HPA") && !acc.contains("ENSG")) doc.addField("antibody", acc);
			else if(db.equals("PeptideAtlas") || db.equals("SRMAtlas")) doc.addField("peptide", acc + ", " + db + ":" + acc);
			else if(db.equals("Ensembl")) doc.addField("ensembl", acc);
			else doc.addField("xrefs", acc + ", " + db + ":" + acc);
		}
		
		// Publications
		List<Publication> publications = entry.getPublications();
		int publi_computed_count = 0;
		int publi_curated_count = 0;
		int publi_large_scale_count = 0;
		for (Publication currpubli : publications) {
			if(currpubli.getIsComputed() == true) publi_computed_count++;
			if(currpubli.getIsCurated() == true) publi_curated_count++;
			if(currpubli.getIsLargeScale() == true) publi_large_scale_count++;
			String title = currpubli.getTitle();
			if(title.length() > 0) doc.addField("publications",title);
			SortedSet<PublicationAuthor> authors = currpubli.getAuthors();
			for (PublicationAuthor currauthor : authors) {
				doc.addField("publications",currauthor.getLastName() + " " + currauthor.getForeName() + " " + currauthor.getInitials());
			}
			Set<DbXref> pubxrefs = currpubli.getDbXrefs();
			for (DbXref pubxref : pubxrefs) {
				String acc =  pubxref.getAccession();
				String db = pubxref.getDatabaseName();
				doc.addField("xrefs", acc + ", " + db + ":" + acc);
			}
		}
		if(publi_computed_count > 0) doc.addField("publi_computed_count", publi_computed_count);
		if(publi_curated_count > 0) doc.addField("publi_curated_count", publi_curated_count);
		if(publi_large_scale_count > 0) doc.addField("publi_large_scale_count", publi_large_scale_count);
		// Now we can compute informational score
		int info_score = 0;
		if(pe_level == 1) info_score=12;
		else if(pe_level == 2) info_score=10;
		else if(pe_level == 3 || pe_level == 4) info_score=8;
		else if(pe_level == 5) info_score=5;
		int coeff = 100*publi_curated_count + 25*publi_computed_count + 10*publi_large_scale_count;
		info_score = coeff * info_score / 10;
		doc.addField("informational_score", info_score);
		
		//doc.addField("orf_names", entry.()); -> together with gene synonyms
		//doc.addField("ec_name", entry.getChromosomalLocations().get(0));
		//doc.addField("expression", entry.());
		//doc.addField("informational_score", entry.());
		//doc.addField("interactions", entry.());
		//doc.addField("region_name", entry.()); corresponds to 'includes' activity (parsed in the getenzyme loop)
		
		/*	
		List<Terminology.TermProperty> properties = terminology.getProperties();
		if (properties != null) {
			doc.addField("properties",TerminologyUtils.convertPropertiesToString(properties));
		} */
		
		// Final CV acs, ancestors and synonyms
		for (String cvac : cv_acs) {
			Terminology term = this.terminologyservice.findTerminologyByAccession(cvac);
			String category = term.getOntology();
			//System.out.println(cvac + ": " + category);
			//if(term == null) System.err.println("problem with " + cvac);
			//else { System.err.println(cvac);
			List<String> ancestors = term.getAncestorAccession();
			if(ancestors != null) 
			  for (String ancestor : ancestors)
                  cv_ancestors_acs.add(ancestor); // No duplicate: this is a Set
			List<String> synonyms = term.getSynonyms();
			if(synonyms != null) { //if (term.getOntology().startsWith("Go")) System.err.println("adding: " + synonyms.get(0));
			  for (String synonym : synonyms)
                  cv_synonyms.add(synonym.trim()); // No duplicate: this is a Set
			      }
		}
		// Index generated sets
		for (String ancestorac : cv_ancestors_acs) {
			doc.addField("cv_ancestors_acs", ancestorac);
			doc.addField("cv_ancestors", this.terminologyservice.findTerminologyByAccession(ancestorac).getName());
		}

		for (String synonym : cv_synonyms) {
			doc.addField("cv_synonyms", synonym);
		}
		
		// Expression
		SortedSet <String> cv_tissues_final = new TreeSet<String>();
		for (String cv : cv_tissues) {
			cv_tissues_final.add(cv); // No duplicate: this is a Set
			if(cv.startsWith("TS-")) {
				Terminology term = this.terminologyservice.findTerminologyByAccession(cv);
				List<String> ancestors = term.getAncestorAccession();
				if(ancestors != null) 
				  for (String ancestorac : ancestors) {
					  cv_tissues_final.add(ancestorac);  // No duplicate: this is a Set
					  cv_tissues_final.add(this.terminologyservice.findTerminologyByAccession(ancestorac).getName());  // No duplicate: this is a Set
				  }
				List<String> synonyms = term.getSynonyms();
				if(synonyms != null) for (String synonym : synonyms)  cv_tissues_final.add(synonym); 
			}
		}
		for (String cv : cv_tissues_final) doc.addField("expression", cv.trim());
		
		return doc;
	}


	public TerminologyService getTerminologysAnnotationSolrIndexerervice() {
		return terminologyservice;
	}

	public void setTerminologyservice(TerminologyService terminologyservice) {
		this.terminologyservice = terminologyservice;
	}

	public DbXrefService getDbxrefSolrIndexerervice() {
		return dbxrefservice;
	}

	public void setDbxrefservice(DbXrefService dbxrefservice) {
		this.dbxrefservice = dbxrefservice;
	}

	
	private Map<Fields, FieldBuilder> fieldsBuilderMap = new HashMap<Fields, FieldBuilder>();
	
	private void buildSolrDocument(Entry entry){
		initializeFieldBuilders();
		SolrInputDocument doc = new SolrInputDocument();

		for(Fields f : Fields.values()){
			FieldBuilder fb = fieldsBuilderMap.get(f);
			Object o = fb.build(entry, f, f.getClazz());
			doc.addField(f.getName(), o);
		}
	}

	private void initializeFieldBuilders() {
	     Reflections reflections = new Reflections("org.nextprot.api.tasks.solr.indexer.entry.impl");
	     Set<Class<?>> entryFieldBuilderClasses = reflections.getTypesAnnotatedWith(EntryFieldBuilder.class);
	     for(Class<?> c : entryFieldBuilderClasses){
	    	 try {
				FieldBuilder fb = (FieldBuilder) c.newInstance();
				for(Fields f: fb.getSupportedFields()){
					fieldsBuilderMap.put(f, fb);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
	     }
	}
	
}
