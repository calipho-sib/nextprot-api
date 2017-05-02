select g.chromosome chromosome, s.unique_name accession, g.band band, g.strand strand, m.is_master as best_location,
g.first_pos_chr firstPosition, g.last_pos_chr lastPosition, s.display_name as displayName,
(select string_agg(sy.synonym_name,' ') from nextprot.identifier_synonyms sy 
where sy.identifier_id=master.identifier_id and sy.is_main is true and sy.cv_type_id=100) as masterGeneNames
from nextprot.gene_identifiers g  
inner join nextprot.sequence_identifiers s on (g.identifier_id = s.identifier_id)  
inner join nextprot.mapping_annotations m on (s.identifier_id = m.reference_identifier_id)  
inner join nextprot.sequence_identifiers master on (master.identifier_id = m.mapped_identifier_id)   
where m.cv_type_id = 3
--and (m.cv_quality_qualifier_id is null or m.cv_quality_qualifier_id !=50) -- should we add this constraint ?
and master.unique_name = :unique_name
