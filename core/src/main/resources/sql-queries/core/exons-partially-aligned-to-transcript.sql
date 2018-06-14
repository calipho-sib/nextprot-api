/**
 * This is a particular case where there is not a 100% match between the mapping from exons to the transcript,
 * this query is only used when the mapping is classified as BRONZE
 */
/* select sig.unique_name gene_name, sit.unique_name transcript_name, sie.unique_name exon, mp_ig.*, mp_eg.* */
select sig.unique_name gene_name, sit.unique_name transcript_name, sie.unique_name exon, (ma_et.rank - 1) rank, (mp_eg.first_pos +1) as first_position, mp_eg.last_pos as last_position
  from nextprot.sequence_identifiers sii
       inner join nextprot.mapping_annotations ma_ig on (ma_ig.mapped_identifier_id = sii.identifier_id and ma_ig.cv_type_id = 8) -- mapping de l isoform sur le gene
       inner join nextprot.sequence_identifiers sig on (ma_ig.reference_identifier_id = sig.identifier_id)
       inner join nextprot.mapping_positions mp_ig on (ma_ig.annotation_id = mp_ig.annotation_id)
       inner join nextprot.mapping_annotations ma_eg on (ma_eg.reference_identifier_id = sig.identifier_id and ma_eg.cv_type_id = 1)
       inner join nextprot.sequence_identifiers sie on (sie.identifier_id = ma_eg.mapped_identifier_id)
       inner join nextprot.mapping_positions mp_eg on (ma_eg.annotation_id = mp_eg.annotation_id)
       inner join nextprot.mapping_annotations ma_tg on (ma_tg.reference_identifier_id = sig.identifier_id and ma_tg.cv_type_id = 2)
       inner join nextprot.sequence_identifiers sit on (sit.identifier_id = ma_tg.mapped_identifier_id)
       inner join nextprot.mapping_annotations ma_et on (ma_et.reference_identifier_id = sit.identifier_id and ma_et.mapped_identifier_id = sie.identifier_id and ma_et.cv_type_id = 9) --exon transcript
       inner join nextprot.mapping_annotations ma_it on (ma_it.reference_identifier_id = sit.identifier_id and ma_it.mapped_identifier_id = sii.identifier_id and ma_it.cv_type_id = 7) --isoform transcript
       inner join nextprot.cv_quality_qualifiers quality on (ma_it.cv_quality_qualifier_id = quality.cv_id)
 where mp_ig.first_pos >= mp_eg.first_pos
   and mp_ig.last_pos <= mp_eg.last_pos
   and mp_ig.last_pos - mp_ig.first_pos > 0
   and sit.unique_name = :transcriptName
   and sig.unique_name = :geneName
   and sii.unique_name = :isoformName
group by gene_name, transcript_name, exon, ma_et.rank, first_position, last_position
order by gene_name, transcript_name, first_position

/**

The orginial query from Anne is the following (represents NX_Q13506-2)

select sii.unique_name, sig.unique_name, sit.unique_name, sie.unique_name, mp_ig.first_pos, mp_ig.last_pos, mp_eg.first_pos, mp_eg.last_pos, quality.cv_name,
       case when (mp_ig.first_pos = mp_eg.first_pos and mp_ig.last_pos = mp_eg.last_pos) then 'COMPLETE' else 'PARTIAL' end as exon_match
  from nextprot.sequence_identifiers sii
       inner join nextprot.mapping_annotations ma_ig on (ma_ig.mapped_identifier_id = sii.identifier_id and ma_ig.cv_type_id = 8)
       inner join nextprot.sequence_identifiers sig on (ma_ig.reference_identifier_id = sig.identifier_id)
       inner join nextprot.mapping_positions mp_ig on (ma_ig.annotation_id = mp_ig.annotation_id)
       inner join nextprot.mapping_annotations ma_eg on (ma_eg.reference_identifier_id = sig.identifier_id and ma_eg.cv_type_id = 1)
       inner join nextprot.sequence_identifiers sie on (sie.identifier_id = ma_eg.mapped_identifier_id)
       inner join nextprot.mapping_positions mp_eg on (ma_eg.annotation_id = mp_eg.annotation_id)
       inner join nextprot.mapping_annotations ma_tg on (ma_tg.reference_identifier_id = sig.identifier_id and ma_tg.cv_type_id = 2)
       inner join nextprot.sequence_identifiers sit on (sit.identifier_id = ma_tg.mapped_identifier_id)
       inner join nextprot.mapping_annotations ma_et on (ma_et.reference_identifier_id = sit.identifier_id and ma_et.mapped_identifier_id = sie.identifier_id and ma_et.cv_type_id = 9)
       inner join nextprot.mapping_annotations ma_it on (ma_it.reference_identifier_id = sit.identifier_id and ma_it.mapped_identifier_id = sii.identifier_id and ma_it.cv_type_id = 7)
       inner join nextprot.cv_quality_qualifiers quality on (ma_it.cv_quality_qualifier_id = quality.cv_id)
 where mp_ig.first_pos >= mp_eg.first_pos
   and mp_ig.last_pos <= mp_eg.last_pos
   and mp_ig.last_pos - mp_ig.first_pos > 0
   and sii.identifier_id = 577534
order by sig.unique_name, sit.unique_name, mp_ig.first_pos
*/