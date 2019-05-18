select 
  g.unique_name as gene_name, 
  null as transcript_name, 
  ex.unique_name as exon, 
  map.rank - 1 as rank,
  mp.first_pos+1 as first_position, 
  mp.last_pos as last_position,
  seq.bio_sequence as sequence
from nextprot.sequence_identifiers g
inner join nextprot.mapping_annotations map on g.identifier_id=map.reference_identifier_id and map.cv_type_id=1
inner join nextprot.sequence_identifiers ex on map.mapped_identifier_id=ex.identifier_id
inner join nextprot.bio_sequences seq on seq.identifier_id=ex.identifier_id
left join nextprot.mapping_positions mp on map.annotation_id=mp.annotation_id
where g.cv_type_id=3 and g.cv_status_id=1 and ex.cv_status_id=1
and g.unique_name = :geneName -- i.e 'NX_ENSG00000178199' 
order by mp.first_pos, mp.last_pos
