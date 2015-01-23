-- number of valid genes mapped to master sequences

select count(distinct ge.identifier_id) as cnt from nextprot.sequence_identifiers ma
inner join nextprot.mapping_annotations mage on (mage.mapped_identifier_id=ma.identifier_id)
inner join nextprot.sequence_identifiers ge on (mage.reference_identifier_id=ge.identifier_id)
where ma.cv_status_id=1 and ma.cv_type_id=1
and ge.cv_status_id=1 and ge.cv_type_id=3
and mage.cv_type_id=3