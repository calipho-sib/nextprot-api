package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class XrefFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		// Xrefs
		List<DbXref> xrefs = entry.getXrefs();
		for (DbXref xref : xrefs) {
			String acc = xref.getAccession();
			String db = xref.getDatabaseName();
			if (db.equals("neXtProtSubmission")) continue;
			//if(db.contains("act")) System.err.println(db);
			if (db.equals("HPA") && !acc.contains("ENSG")) { // HPA with ENSG are for expression
				//System.err.println("AB: " + acc);
				addField(Fields.ANTIBODY, acc);
			}
            if (db.equals("Ensembl")) {
				addField(Fields.ENSEMBL, acc);
			} 
            // There is an inconsistency in the way EMBL xref properties are managed: 
            // for genomic sequences EAW78410.1 -> molecule type=protein, the pid appears as an individual xref
            // and the EMBL acc is a property EAW78410.1 -> genomic sequence ID=CH471052
            // but for mrnas BC040557 -> protein sequence ID=AAH40557.1, the pid is just a property of the xref...
            if (!(db.equals("PeptideAtlas") || db.equals("SRMAtlas"))) { // These are indexed under the 'peptide' field
            	
            	//if(!acc.startsWith("EBI-")) // These are indexed under the 'interaction' field
				   //addField(Fields.XREFS,db + ":" + acc + ", " + acc);
				//if(db.equals("IntAct"))
				   if(db.equals("EMBL")) {
				   //System.err.println(db + ":" + acc);	   
				   String propvalue = xref.getPropertyValue("protein sequence ID");
				   if(propvalue != null) {
					   //System.err.println("indexing 'protein sequence ID' " + propvalue);
					   //addField(Fields.XREFS,"protein sequence ID:" + propvalue + ", " + propvalue);
					   addField(Fields.XREFS,"EMBL:" + propvalue + ", " + propvalue);
					   //System.err.println("indexing 'EMBL' " + acc);
					   addField(Fields.XREFS,"EMBL:" + acc + ", " + acc);
				   }
				   else {
					   propvalue = xref.getPropertyValue("genomic sequence ID"); 
				       if(propvalue != null) {
					     //System.err.println("indexing 'genomic sequence ID' " + propvalue);
						   //addField(Fields.XREFS,"genomic sequence ID:" + propvalue + ", " + propvalue);
					     //System.err.println("indexing 'EMBL' " + acc); // This is definitely wrong, should be next line
						   addField(Fields.XREFS,"EMBL:" + acc + ", " + acc);
					     //System.err.println("indexing 'protein sequence ID' " + acc);
				       }
					   else if (!acc.contains(".")) {
						   //System.err.println("indexing 'EMBL' " + acc);   
						   addField(Fields.XREFS,"EMBL:" + acc + ", " + acc);
					   }
				   }
				   //List<DbXrefProperty> xrefprops = xref.getProperties();
				   //for(DbXrefProperty prop : xrefprops)
				  //System.err.println(db + ":" + acc + " -> " + prop.getName() + "=" + prop.getValue());
				  //System.err.println(db + " -> " + xref.getDatabaseCategory());
				   }
				   // maybe process all these cases with category "Family and domain databases"
				   else if(db.equals("InterPro") || db.equals("Pfam") || db.equals("PROSITE") || db.equals("TIGRFAMs") || db.equals("SMART")
						   || db.equals("PRINTS") || db.equals("HAMAP") || db.equals("PeroxiBase") || db.equals("PIRSF") || db.equals("PIR")) {
					   // Also add db entry names
					   addField(Fields.XREFS,db + ":" + acc + ", " + acc);
					   addField(Fields.XREFS,db + ":" + xref.getPropertyValue("entry name") + ", " + xref.getPropertyValue("entry name"));
					   //addField(Fields.XREFS,"entry name:" + xref.getPropertyValue("entry name") + ", " + xref.getPropertyValue("entry name"));
				   }
				   else if(db.equals("TCDB") || db.equals("CAZy") || db.equals("ESTHER")) {
					   // Also add db entry names
					   addField(Fields.XREFS,db + ":" + acc + ", " + acc);
					   addField(Fields.XREFS,db + ":" + xref.getPropertyValue("family name") + ", " + xref.getPropertyValue("family name"));
					   //addField(Fields.XREFS,"family name:" + xref.getPropertyValue("family name") + ", " + xref.getPropertyValue("family name"));
				   }
				  else if(db.equals("UniPathway")) {
					   addField(Fields.XREFS,db + ":" + acc + ", " + acc);
					   String reactID = xref.getPropertyValue("reaction ID");
					   if(reactID != null)
						   addField(Fields.XREFS,db + ":" + reactID + ", " + reactID);
					      //System.err.println(db + ":" + acc + " -> " + prop.getName() + "=" + prop.getValue());
				   }
				   else addField(Fields.XREFS,db + ":" + acc + ", " + acc);
			}
	
		}

		// It is weird to have to go thru this to get the CAB antibodies, they should come with getXrefs()
		Set<String> CABSet = new HashSet<String>();
		List<Annotation> annots = entry.getAnnotations();
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			//System.err.println("Annot: " + category);
			if (category.equals("expression info") || category.equals("subcellular location")) {
				List<AnnotationEvidence> evlist = currannot.getEvidences();
				for (AnnotationEvidence evidence : evlist) { 
					String AB = evidence.getPropertyValue("antibodies acc");
					if(AB != null && AB.contains("CAB")) {
						// Several may appear (eg:CAB025488; HPA028825; CAB058697) , keep only CABs 
						//System.err.println("AB: " + AB);
						Set<String> localCABSet = new HashSet<String>(Arrays.asList(AB.split("; ")));
						for(String CAB : localCABSet)
							if(CAB.startsWith("CAB"))
						      CABSet.add(CAB);
					}
				}
			}
			else if (category.equals("pathway")) { // Same remark
				//DbXref parentXref = currannot.getParentXref();
				//if(parentXref != null && parentXref.getDatabaseName().equals("Reactome"))
					addField(Fields.XREFS,"Pathway:" + currannot.getDescription() + ", " + currannot.getDescription());
				//System.err.println(parentXref.getDatabaseName());
				//System.err.println(currannot.getDescription());
			}
			else if (category.equals("disease")) { // Same remark
				DbXref parentXref = currannot.getParentXref();
				if(parentXref != null && parentXref.getDatabaseName().equals("Orphanet")) {
					String disName = parentXref.getPropertyValue("disease");
				  addField(Fields.XREFS,"Disease:" + disName + ", " + disName);
				  //System.err.println(disName);
				}
			}
			/*else if (category.equals("subcellular location")) { // Same remark, this one is terrible
				List<AnnotationEvidence> evlist = currannot.getEvidences();
				for (AnnotationEvidence evidence : evlist) { 
					String AB = evidence.getPropertyValue("antibodies acc");
					if(AB != null && AB.contains("CAB")) {
						// Several may appear (eg:CAB025488; HPA028825; CAB058697) , keep only CABs 
						//System.err.println("AB: " + AB);
						Set<String> localCABSet = new HashSet<String>(Arrays.asList(AB.split("; ")));
						for(String CAB : localCABSet)
							if(CAB.startsWith("CAB"))
						      CABSet.add(CAB);

					}
				}
			}*/
			else if (category.equals("SmallMoleculeInteraction")) { // Same remark
				//DbXref parentXref = currannot.getParentXref();
				//if(parentXref != null && parentXref.getDatabaseName().equals("Orphanet")) {
					//String disName = parentXref.getPropertyValue("disease");
				  addField(Fields.XREFS,"generic name:" + currannot.getDescription() + ", " + currannot.getDescription());
				  //System.err.println(currannot.getDescription());
				//}
			} //else if(category.contains("subcel")) System.err.println(category);
		}
		if(CABSet.size() > 0) 
		  for (String CAB : CABSet) {
			  addField(Fields.ANTIBODY, CAB);
			  addField(Fields.XREFS, "HPA:" + CAB+ ", " + CAB);
		  }

		// Isoform ids
		List<Isoform> isoforms = entry.getIsoforms();
		for (Isoform iso : isoforms) {
			String isoId = iso.getUniqueName().substring(3);
			//System.err.println(isoId);
			addField(Fields.XREFS,"isoform ID:" + isoId + ", " + isoId);
		}
		// Xrefs to publications (PubMed, DOIs)
		for (Publication currpubli : entry.getPublications()) {
			Set<DbXref> pubxrefs = currpubli.getDbXrefs();
			for (DbXref pubxref : pubxrefs) {
				String acc = pubxref.getAccession().trim(); // It happens to have a trailing \t (like 10.1080/13547500802063240 in NX_P14635)
				//if(acc.startsWith("10.1080")) System.err.println(acc);
				String db = pubxref.getDatabaseName();
				//if (!db.equals("neXtProtSubmission"))
				   addField(Fields.XREFS,db + ":" + acc + ", " + acc);
			}
		}

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.XREFS, Fields.ENSEMBL, Fields.ANTIBODY);
	}

}
