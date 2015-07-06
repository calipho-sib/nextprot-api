package org.nextprot.api.tasks.solr.indexer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.dao.impl.DbXrefDaoImpl;
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
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.Overview.EntityName;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.domain.Interaction;
//import org.nextprot.api.core.service.TerminologyService;
//import org.nextprot.api.import org.nextprot.api.core.domain.AntibodyMapping;
//import org.nextprot.api.core.utils.TerminologyUtils;
//import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.TerminologyService;


public class AnnotationSolrIndexer extends SolrIndexer<Entry> {
	
	private TerminologyService terminologyservice;

	public AnnotationSolrIndexer(String url) {
		super(url);
	}

	@Override
	public SolrInputDocument convertToSolrDocument(Entry entry) {
		SolrInputDocument doc = new SolrInputDocument();
		Set <String> cv_acs = new HashSet<String>();
		Set <String> cv_ancestors_acs = new HashSet<String>();
		String id = entry.getUniqueName();
		doc.addField("id", id);
		doc.addField("idsp0", id);
		doc.addField("recommended_ac", id.substring(3));
		Overview ovv = entry.getOverview(); 
		doc.addField("protein_existence", ovv.getProteinExistence());
		doc.addField("pe_level", ovv.getProteinExistenceLevel());
		//doc.addField("isoform_num", entry.getIsoforms().size());
		String precname = ovv.getMainProteinName();
		//System.err.println(id + " " + precname);
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
				doc.addField("region_name", altname.getName());
				//System.err.println(id + " fromincludes: " + altname.getName());
				List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				for (EntityName ecname : paltnames) {
				    //doc.addField("ec_name", ecname.getName());
					//System.err.println(id + " fromincludes: " + ecname.getName());
				}
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
		
		List <ChromosomalLocation> chrlocs = entry.getChromosomalLocations();
		String chrloc = null;
		//String tststring = null;
		for (ChromosomalLocation currloc : chrlocs) {
			if(chrloc == null) chrloc = currloc.getChromosome();
			String band = currloc.getBand();
			if(band != null) chrloc += band;
		}
		//System.err.println("adding chr_loc: " + chrloc);
		doc.addField("chr_loc", chrloc);
		
		/* DbXrefDaoImpl dao = new DbXrefDaoImpl();
		Set<DbXref> intactdbrefs = dao.findEntryInteractionInteractantsXrefs(id);
		if(intactdbrefs != null)
		for (DbXref intactdbref : intactdbrefs) {	
			System.err.println(intactdbref.getProperties());
			List<DbXrefProperty> xrefprops =  intactdbref.getProperties();
			for (DbXrefProperty xrefprop : xrefprops) {
				if(xrefprop.getName().equals("gene designation")) {
					System.err.println("gene: " + xrefprop.getValue());
				}
			}
		} */
		
		List<Annotation> annots =  entry.getAnnotations();
		int cvac_cnt = 0;
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			//System.err.println(category);
			/* if(category.contains("Binary"))  {
				//System.err.println(category + " : " + currannot.getUniqueName());
				List<AnnotationProperty> annotprops =  currannot.getProperties();
				for (AnnotationProperty annotprop : annotprops) {
					if(annotprop.getName().equals("interactant")) {
						long dbrefid = Integer.parseInt(annotprop.getValue());
						//DbXref xref = 
					System.err.println("dbrefid: " + annotprop.getValue());
					}
				}
			} */
			if(category.equals("function")) doc.addField("function_desc", currannot.getDescription());
			else if(category.equals("subcellular location") || category.equals("go cellular component") ||
					category.equals("domain") || category.equals("repeat") ||  category.equals("zinc finger region") ||
					category.equals("go molecular function") || category.equals("go biological process") ||
					category.equals("tissue specificity") ||
					category.equals("uniprot keyword")) {
				String cvac = currannot.getCvTermAccessionCode();
				if(cvac != null) {
				   doc.addField("cv_acs", cvac);
				   cvac_cnt++;
				   cv_acs.add(cvac); // No duplicates: this is a Set
				   doc.addField("cv_names", currannot.getCvTermName()); 
				   }
			}
			else if(category.equals("region of interest") || category.equals("interacting region")) { // compositionally biased region ?
				//doc.addField("?", currannot.getDescription()); 
			}
			else {
				doc.addField("annotations", currannot.getDescription());
			//System.err.println(category + " : " + currannot.getDescription());
			} // or maybe not 'else'
		}
		
		// Identifiers
		List <Identifier> identifiers = entry.getIdentifiers();
		for (Identifier currident : identifiers) {
			String idtype = currident.getType();
			//System.err.println("type: " + idtype);
			if(idtype.equals("Secondary AC")) doc.addField("alternative_acs", currident.getName());
			else if (idtype.equals("IMAGE") || idtype.equals("FLJ") || idtype.equals("MGC") || idtype.equals("DKFZ")) doc.addField("clone_name", currident.getName());
			else if (idtype.equals("Illumina") || idtype.equals("Affymetrix")) doc.addField("microarray_probe", currident.getName());
		}
		
		List<Terminology> enzymes = entry.getEnzymes();
		for (Terminology currenzyme : enzymes) {
			//System.err.println(id + " fromenz: " + currenzyme.getAccession());
			doc.addField("cv_acs", currenzyme.getAccession());
			cvac_cnt++;
			cv_acs.add(currenzyme.getAccession());
			doc.addField("cv_names", currenzyme.getName());
		}

		List<Interaction> interactions = entry.getInteractions();
		for (Interaction currinteraction : interactions) {
			List<Interactant> interactants = currinteraction.getInteractants();
			for (Interactant currinteractant : interactants) {
			     //System.err.println(currinteractant.getNextprotAccession() + " " + currinteractant.getUrl());
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
			if(db.equals("neXtProt")) continue; // Internal stuff like NX_VG_10_51732257_248
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
		
		//doc.addField("orf_names", entry.());
		//doc.addField("cv_synonyms", entry.());
		//doc.addField("ec_name", entry.getChromosomalLocations().get(0));
		//doc.addField("expression", entry.());
		//doc.addField("gene_band", entry.());
		//doc.addField("informational_score", entry.());
		//doc.addField("interactions", entry.());
		//doc.addField("region_name", entry.());
		//doc.addField("uniprot_name", entry.());
		
		/*
		 *  List<String> synonstrings = terminology.getSynonyms();
		if (synonstrings != null) {
			int i = synonstrings.size();
			StringBuilder sb = new StringBuilder();
			for (String syn: synonstrings) {sb.append(syn); if (--i != 0) sb.append(" | "); }
			doc.addField("synonyms",sb.toString());
		}
		
		List<Terminology.TermProperty> properties = terminology.getProperties();
		if (properties != null) {
			doc.addField("properties",TerminologyUtils.convertPropertiesToString(properties));
		} */
		
		// Get cv ancestors from a unique list of cv_acs
		//System.err.println(id + " uniq: " + cv_acs.size() + " vs " + cvac_cnt); 
		//if(cv_acs.size() < cvac_cnt-2)
		//System.err.println(cv_acs.size());
		
		// Final  CV acs and ancestors
		for (String cvac : cv_acs) {
			//System.out.println(cvac);
			Terminology term = this.terminologyservice.findTerminologyByAccession(cvac);
			//if(term == null) System.err.println("problem with " + cvac);
			//else { System.err.println(cvac);
			List<String> ancestors = term.getAncestorAccession();
			if(ancestors != null) 
			  for (String ancestor : ancestors)
                  cv_ancestors_acs.add(ancestor); // No duplicate: this is a Set
			}
		
		for (String ancestorac : cv_ancestors_acs) {
			doc.addField("cv_ancestors_acs", ancestorac);
			doc.addField("cv_ancestors", this.terminologyservice.findTerminologyByAccession(ancestorac).getName());
		}
		return doc;
	}


	public TerminologyService getTerminologysAnnotationSolrIndexerervice() {
		return terminologyservice;
	}

	public void setTerminologyservice(TerminologyService terminologyservice) {
		this.terminologyservice = terminologyservice;
	}

	public static long sortChr(String chr) {
		chr=chr.trim();
		
		String[] chr_loc=chr.split("([pq]|cen)");  // split on p or q
		long f_chr0=1000000; 	 
		long f_q=50000; 	 
		long f_chr1=1000; 		
		int  max_chr=50;		// max chr localtion after pq 
		long chr0, chr1;

		
		// push unknown chromosome at the end 
		if (chr.indexOf("unknown")>-1 || chr.equals("")) { return f_chr0*30; }
		if(chr_loc[0].equalsIgnoreCase("x")){ chr0=23*f_chr0;}
		else if(chr_loc[0].equalsIgnoreCase("y")) { chr0=24*f_chr0; }
		else if(chr_loc[0].equalsIgnoreCase("mt")) { chr0=25*f_chr0;}
		else { chr0=Integer.parseInt(chr_loc[0])*f_chr0; }
		
		// sort(cen) = 10E5*XX + 10E4-1
		if (chr.indexOf("cen")>-1)	return chr0+f_q-1;			
		// sort(chr) = 10E5*XX 
		if (chr_loc.length==1) return (chr0);
		
		// extract double value from digits after p or q
		chr1=(long)( Double.parseDouble(chr_loc[1].split("[-,]")[0]) * f_chr1);
		
		// sort(q) = 10E5*XX + 10E4 + 100*YY
		if(chr.indexOf('q')>-1) return chr0+chr1+f_q;			
		
		// sort(p) = 10E6*XX + 1000*(45-YY)  //descending order
		return chr0 + f_chr1 * max_chr - chr1;
	}
}
