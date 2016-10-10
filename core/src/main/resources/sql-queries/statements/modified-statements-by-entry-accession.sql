select *
from nxflat.entry_mapped_statements ms
where subject_annotation_ids is not null
and ms.entry_accession = :accession