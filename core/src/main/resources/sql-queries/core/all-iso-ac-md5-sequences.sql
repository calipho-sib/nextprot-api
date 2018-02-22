select iso.unique_name as accession, s.md5 as md5, s.bio_sequence
from nextprot.sequence_identifiers iso 
inner join nextprot.bio_sequences s on (iso.identifier_id=s.identifier_id)
where iso.cv_type_id=2 and iso.cv_status_id=1
order by iso.unique_name