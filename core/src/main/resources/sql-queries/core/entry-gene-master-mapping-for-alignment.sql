-- example: unique_name = NX_P47710
-- mapping type 3 = gene-master mapping
-- in order to return the single gene data used for alignment 
-- we need criteria is_master = true, db.cv_name = Ensembl, quality != 100
select 
  gene.unique_name as gene_name, 
  mapping.rank,mp.first_pos  + 1 as first_position,
  mp.last_pos as last_position 
from nextprot.sequence_identifiers gene   
inner join nextprot.mapping_annotations mapping on (mapping.reference_identifier_id = gene.identifier_id and mapping.cv_type_id=3)  
inner join nextprot.sequence_identifiers master on (master.identifier_id = mapping.mapped_identifier_id) 
inner join nextprot.mapping_positions mp on (mapping.annotation_id=mp.annotation_id)  
inner join nextprot.db_xrefs xrefs on (gene.db_xref_id = xrefs.resource_id)   
inner join nextprot.cv_databases db on (xrefs.cv_database_id = db.cv_id)
where mapping.cv_quality_qualifier_id != 100 and mapping.is_master is true and db.cv_name = 'Ensembl'
and master.unique_name = :entryName
order by first_pos, last_pos
