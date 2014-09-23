-- -------------------------------------------------------------------------------------------------
-- Number of proteotypic peptides:
-- peptides linked to 1 and only one distinct master 
-- and being connected via the mappings ma->iso->pep and ma->pep
-- and having at least one mapping_postions record
-- -------------------------------------------------------------------------------------------------

select count(*) as cnt from (
select pep.unique_name from 
nextprot.sequence_identifiers ma,
nextprot.mapping_annotations ma_iso,
nextprot.sequence_identifiers iso,
nextprot.mapping_annotations iso_pep,
nextprot.sequence_identifiers pep,
nextprot.mapping_annotations ma_pep,
nextprot.mapping_positions mp
where ma.cv_status_id=1 and ma.cv_type_id=1 and ma.identifier_id=ma_iso.reference_identifier_id
and iso.cv_status_id=1 and iso.cv_type_id=2 and iso.identifier_id=ma_iso.mapped_identifier_id
and iso.cv_status_id=1 and iso.cv_type_id=2 and iso.identifier_id=iso_pep.reference_identifier_id 
and pep.cv_status_id=1 and pep.cv_type_id=7 
and pep.identifier_id=iso_pep.mapped_identifier_id
and ma.cv_status_id=1 and ma.cv_type_id=1 and ma.identifier_id=ma_pep.reference_identifier_id
and pep.cv_status_id=1 and pep.cv_type_id=7 and pep.identifier_id=ma_pep.mapped_identifier_id
and mp.annotation_id=iso_pep.annotation_id
and ma_iso.cv_type_id=4
and iso_pep.cv_type_id=11
and ma_pep.cv_type_id=10
group by pep.unique_name
having (count(distinct ma.unique_name)=1)
) a
