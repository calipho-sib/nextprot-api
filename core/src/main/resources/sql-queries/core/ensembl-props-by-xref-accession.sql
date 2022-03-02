select
trn.db_xref_id as enst_xref_id,
ti.annotation_id as enst_iso_map_id, -- unique for each enst-iso pair
ti.cv_quality_qualifier_id as enst_iso_map_qual, 
substr(trn.unique_name,4) as enst, 
substr(iso.unique_name,4) as iso, 
substr(gen.unique_name,4) as ensg,
(select px.accession 
from nextprot.identifier_resource_assoc tp
inner join nextprot.db_xrefs px on (tp.resource_id = px.resource_id and px.accession like 'ENSP%')
where tp.identifier_id = trn.identifier_id
) as ensp
from nextprot.sequence_identifiers mst
inner join nextprot.mapping_annotations gm on (gm.mapped_identifier_id=mst.identifier_id and gm.cv_type_id = 3 and gm.cv_quality_qualifier_id !=100)
inner join nextprot.sequence_identifiers gen on (gm.reference_identifier_id=gen.identifier_id and gen.cv_status_id=1 and gen.cv_type_id=3)
inner join nextprot.mapping_annotations gt on (gt.reference_identifier_id=gen.identifier_id and gt.cv_type_id=2)
inner join nextprot.sequence_identifiers trn on (gt.mapped_identifier_id=trn.identifier_id and trn.cv_status_id=1 and trn.cv_type_id=5)
inner join nextprot.mapping_annotations ti on (ti.reference_identifier_id=trn.identifier_id and ti.cv_type_id=7)
inner join nextprot.sequence_identifiers iso on (ti.mapped_identifier_id=iso.identifier_id and iso.cv_status_id = 1 and iso.cv_type_id=2)
inner join nextprot.mapping_annotations mi on (mst.identifier_id=mi.reference_identifier_id and iso.identifier_id=mi.mapped_identifier_id and mi.cv_type_id=4)
where mst.cv_status_id = 1 and mst.cv_type_id = 1
and trn.db_xref_id in (:xrefIds) -- ENST xref ids
