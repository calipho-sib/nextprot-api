select *
from mapped_statements ms
where subject_statement_ids is not null
and ms.entry_accession = :accession