select gene.identifier_id, dbs.cv_name, xrefs.accession, mapping.is_master
from nextprot.sequence_identifiers gene   
inner join nextprot.mapping_annotations mapping on (mapping.reference_identifier_id = gene.identifier_id and mapping.cv_quality_qualifier_id != 100 and mapping.cv_type_id=3)   
inner join nextprot.sequence_identifiers master on (master.identifier_id = mapping.mapped_identifier_id)   
inner join nextprot.db_xrefs xrefs on (gene.db_xref_id = xrefs.resource_id)   
inner join nextprot.cv_databases dbs on (xrefs.cv_database_id = dbs.cv_id)   
where gene.cv_status_id=1 and master.cv_status_id=1
and master.unique_name = :unique_name
and dbs.cv_name = 'Ensembl' -- all dbs.cv_name in (HGNC, Ensembl, neXtProt)