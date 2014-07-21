select distinct si.unique_name from nextprot.mapping_annotations ma 
inner join nextprot.gene_identifiers gi on ma.reference_identifier_id = gi.identifier_id 
inner join nextprot.sequence_identifiers si on ma.mapped_identifier_id = si.identifier_id 
where gi.chromosome = :chromossome
and ma.cv_type_id = 3 and si.cv_status_id=1  and si.cv_type_id=1