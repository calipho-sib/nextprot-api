select *
from mapped_statements_next ms
where ms.annotation_category != 'phenotype' 
and ms.entry_accession = :accession