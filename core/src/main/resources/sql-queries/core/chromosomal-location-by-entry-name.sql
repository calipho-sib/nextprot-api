select gi.chromosome, gs.unique_name as accession, 
gi.band, gi.strand, map.is_master as best_location, 
gi.first_pos_chr firstPosition, gi.last_pos_chr lastPosition, 
gs.display_name as displayName,
(select string_agg(syn.synonym_name,' ') from nextprot.identifier_synonyms syn where ms.identifier_id = syn.identifier_id and syn.cv_type_id = 100 and syn.is_main=true) as masterGeneNames,
(select string_agg(syn.synonym_name,' ') from nextprot.identifier_synonyms syn where gs.identifier_id = syn.identifier_id and syn.cv_type_id = 1   and syn.is_main=true) as geneGeneNames,
(select cv_name from nextprot.cv_quality_qualifiers q where q.cv_id=map.cv_quality_qualifier_id) as quality
from nextprot.gene_identifiers gi
inner join nextprot.sequence_identifiers gs on (gi.identifier_id=gs.identifier_id and gs.cv_type_id=3 and gs.cv_status_id=1)
inner join nextprot.mapping_annotations map on (gi.identifier_id=map.reference_identifier_id and map.cv_type_id=3 and map.cv_quality_qualifier_id !=100)
inner join nextprot.sequence_identifiers ms on (map.mapped_identifier_id=ms.identifier_id and ms.cv_status_id=1 and ms.cv_type_id=1 )
where ms.unique_name = :unique_name
