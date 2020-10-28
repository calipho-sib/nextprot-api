select context_id,
(select x.accession from nextprot.cv_terms t inner join nextprot.db_xrefs x on (t.db_xref_id=x.resource_id) where t.cv_id=ec.tissue_id) as tissueAC,
(select x.accession from nextprot.cv_terms t inner join nextprot.db_xrefs x on (t.db_xref_id=x.resource_id) where t.cv_id=ec.developmental_stage_id) as developmentalStageAC,
(select x.accession from nextprot.cv_terms t inner join nextprot.db_xrefs x on (t.db_xref_id=x.resource_id) where t.cv_id=ec.cell_line_id) as cellLineAC,
(select x.accession from nextprot.cv_terms t inner join nextprot.db_xrefs x on (t.db_xref_id=x.resource_id) where t.cv_id=ec.disease_id) as diseaseAC,
(select x.accession from nextprot.cv_terms t inner join nextprot.db_xrefs x on (t.db_xref_id=x.resource_id) where t.cv_id=ec.detection_method_id) as detectionMethodAC,
(select x.accession from nextprot.cv_terms t inner join nextprot.db_xrefs x on (t.db_xref_id=x.resource_id) where t.cv_id=ec.organelle_id) as organelleAC,
ec.metadata_id as metadataId,
ec.md5 as md5
from nextprot.experimental_contexts ec 
