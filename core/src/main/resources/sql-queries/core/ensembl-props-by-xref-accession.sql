select tx.resource_id as db_xref_id, m.identifier_id as entry_id,
 m.unique_name as entry_ac,gx.accession as gene_ac,
 tx.accession as transcript_ac , px.accession as protein_ac,
 gt.annotation_id as gt_link_id, tp.assoc_id as tp_link_id
from nextprot.sequence_identifiers m
  inner join nextprot.mapping_annotations mg on (m.identifier_id = mg.mapped_identifier_id and mg.cv_type_id = 3 and mg.cv_quality_qualifier_id !=100)
  inner join nextprot.sequence_identifiers g on (mg.reference_identifier_id = g.identifier_id and g.cv_type_id = 3)
  inner join nextprot.db_xrefs gx on (g.db_xref_id = gx.resource_id)
  inner join nextprot.mapping_annotations gt on (g.identifier_id = gt.reference_identifier_id and gt.cv_type_id = 2)
  inner join nextprot.sequence_identifiers t on (gt.mapped_identifier_id = t.identifier_id and t.cv_type_id = 5)
  inner join nextprot.db_xrefs tx on (t.db_xref_id = tx.resource_id)
  inner join nextprot.identifier_resource_assoc tp on (tp.identifier_id = t.identifier_id)
  inner join nextprot.db_xrefs px on (tp.resource_id = px.resource_id and px.accession like 'ENSP%')
where tx.resource_id in (:xrefIds)
  and m.unique_name = :uniqueName