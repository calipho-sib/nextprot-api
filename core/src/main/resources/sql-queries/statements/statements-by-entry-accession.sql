select *
from nxflat.entry_mapped_statements ms
where subject_annotation_ids is null
and ms.entry_accession = :accession