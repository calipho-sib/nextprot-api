select coalesce(isoform_id,entry_ac) as nx_ac, refseq_id, nucleotide_sequence_id as refseq_nsid from (
select si.unique_name as entry_ac, x.accession as refseq_id, 
(select 'NX_' || rp.property_value from  nextprot.resource_properties rp where rp.resource_id=x.resource_id and rp.property_name='isoform ID') as isoform_id,
(select rp.property_value from nextprot.resource_properties rp where rp.resource_id=x.resource_id and rp.property_name='nucleotide sequence ID') as nucleotide_sequence_id
from nextprot.sequence_identifiers si
inner join nextprot.identifier_resource_assoc ira on (si.identifier_id=ira.identifier_id)
inner join nextprot.db_xrefs x on (ira.resource_id=x.resource_id and x.cv_database_id=114)
where si.cv_status_id=1
) z
order by nx_ac, refseq_id

