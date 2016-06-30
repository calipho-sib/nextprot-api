select * from mapped_statements ms 
where annotation_category != 'phenotype' 
and entry_accession = :entry_accession