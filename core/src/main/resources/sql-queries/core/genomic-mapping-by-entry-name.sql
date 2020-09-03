select gene.identifier_id, dbs.cv_name, xrefs.accession, mapping.is_master
from nextprot.sequence_identifiers gene   
inner join nextprot.mapping_annotations mapping on (mapping.reference_identifier_id = gene.identifier_id and mapping.cv_quality_qualifier_id != 100)   
inner join nextprot.cv_mapping_annotation_types mapping_types on (mapping.cv_type_id = mapping_types.cv_id)   
inner join nextprot.sequence_identifiers master on (master.identifier_id = mapping.mapped_identifier_id)   
inner join nextprot.db_xrefs xrefs on (gene.db_xref_id = xrefs.resource_id)   
inner join nextprot.cv_databases dbs on (xrefs.cv_database_id = dbs.cv_id)   
where mapping_types.cv_name = 'MASTER_SEQUENCE_GENE'  
and gene.cv_status_id=1 and master.cv_status_id=1
and master.unique_name = :unique_name
and dbs.cv_name = 'Ensembl' -- all dbs.cv_name in (HGNC, Ensembl, neXtProt)
