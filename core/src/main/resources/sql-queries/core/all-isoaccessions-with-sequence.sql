select iso.unique_name as iso_accession, seq.bio_sequence
from nextprot.sequence_identifiers iso
inner join nextprot.bio_sequences seq on (iso.identifier_id=seq.identifier_id)
where iso.cv_type_id=2 and iso.cv_status_id=1
