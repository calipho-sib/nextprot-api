package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

@Service
public class XrefSolrFieldCollector extends EntrySolrFieldCollector {

	@Autowired
	private DbXrefService dbXrefService;

	@Autowired
	private AnnotationService annotationService;

	@Autowired
	private IsoformService isoformService;

	@Autowired
	private PublicationService publicationService;

    @Override
    public void collect(Map<EntrySolrField, Object> fields, String entryAccession, boolean gold) {

        String[] extraNameCat = {"entry name", "family name", "allergen name", "reaction ID", "toxin name"};
        // Xrefs
        List<DbXref> xrefs = dbXrefService.findDbXrefsByMaster(entryAccession);
        for (DbXref xref : xrefs) {
            String acc = xref.getAccession();
            String db = xref.getDatabaseName();
            if (db.equals(NEXTPROT_SUBMISSION.getName())) continue;
            if (db.equals(HPA.getName()) && !acc.contains(ENSG.getName())) { // HPA with ENSG are for expression
                addEntrySolrFieldValue(fields, EntrySolrField.ANTIBODY, acc);
            }
            if (db.equals(ENSEMBL.getName())) {
                addEntrySolrFieldValue(fields, EntrySolrField.ENSEMBL, acc);
            }
            // There is an inconsistency in the way EMBL xref properties are managed: 
            // for genomic sequences EAW78410.1 -> molecule type=protein, the pid appears as an individual xref
            // and the EMBL acc is a property EAW78410.1 -> genomic sequence ID=CH471052
            // but for mrnas BC040557 -> protein sequence ID=AAH40557.1, the pid is just a property of the xref...
            if (!(db.equals(PEPTIDE_ATLAS.getName()) || db.equals(SRM_ATLAS.getName()))) { // These are indexed under the 'peptide' field

                if (db.equals(EMBL.getName())) {
                    String propvalue = xref.getPropertyValue("protein sequence ID");
                    if (propvalue != null) {
                        addEntrySolrFieldValue(fields, EntrySolrField.XREFS, "EMBL:" + propvalue + ", " + propvalue);
                        addEntrySolrFieldValue(fields, EntrySolrField.XREFS, "EMBL:" + acc + ", " + acc);
                    } else {
                        propvalue = xref.getPropertyValue("genomic sequence ID");
                        if (propvalue != null || !acc.contains(".")) {
                            addEntrySolrFieldValue(fields, EntrySolrField.XREFS, "EMBL:" + acc + ", " + acc);
                        }
                    }
                } else {
                    addEntrySolrFieldValue(fields, EntrySolrField.XREFS, db + ":" + acc + ", " + acc);
                    for (String category : extraNameCat) {
                        String extraName = xref.getPropertyValue(category);
                        if (extraName != null) { // Can be found for dbs: "InterPro", "Pfam", "PROSITE"), "TIGRFAMs", "SMART", "PRINTS", "HAMAP",
                            // "PeroxiBase", "PIRSF", "PIR", "TCDB", "CAZy", "ESTHER", UniPathway
                            addEntrySolrFieldValue(fields, EntrySolrField.XREFS, db + ":" + extraName + ", " + extraName);
                            break;
                        }
                    }
                }
            }
        }

        // It is weird to have to go thru this to get the CAB antibodies, they should come with getXrefs()
        List<Annotation> annots = annotationService.findAnnotations(entryAccession);
        for (Annotation currannot : annots) {
            String category = currannot.getCategory();

            if ("pathway".equals(category)) {
                addEntrySolrFieldValue(fields, EntrySolrField.XREFS, "Pathway:" + currannot.getDescription() + ", " + currannot.getDescription());
            } else if ("disease".equals(category)) { // Same remark
                DbXref parentXref = currannot.getParentXref();
                if (parentXref != null && parentXref.getDatabaseName().equals(ORPHANET.getName())) {
                    String disName = parentXref.getPropertyValue("disease");
                    addEntrySolrFieldValue(fields, EntrySolrField.XREFS, "Disease:" + disName + ", " + disName);
                }
            } else if ("SmallMoleculeInteraction".equals(category)) { // Same remark
                addEntrySolrFieldValue(fields, EntrySolrField.XREFS, "generic name:" + currannot.getDescription() + ", " + currannot.getDescription());
            }
        }

        // Isoform ids
        List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryAccession);
        for (Isoform iso : isoforms) {
            String nxIsoformId = iso.getIsoformAccession();
            String isoId = nxIsoformId.substring(3);
            addEntrySolrFieldValue(fields, EntrySolrField.XREFS, "isoform ID:" + isoId + ", " + isoId);
            addEntrySolrFieldValue(fields, EntrySolrField.XREFS, "NX isoform ID:" + nxIsoformId + ", " + nxIsoformId);
        }
        // Xrefs to publications (PubMed, DOIs)
        for (Publication currpubli : publicationService.findPublicationsByEntryName(entryAccession)) {
            List<PublicationDbXref> pubxrefs = currpubli.getDbXrefs();
            for (DbXref pubxref : pubxrefs) {
                String acc = pubxref.getAccession().trim(); // It happens to have a trailing \t (like 10.1080/13547500802063240 in NX_P14635)
                String db = pubxref.getDatabaseName();
                addEntrySolrFieldValue(fields, EntrySolrField.XREFS, db + ":" + acc + ", " + acc);
            }
        }
    }

    @Override
    public Collection<EntrySolrField> getCollectedFields() {
        return Arrays.asList(EntrySolrField.XREFS, EntrySolrField.ENSEMBL, EntrySolrField.ANTIBODY);
    }
}
