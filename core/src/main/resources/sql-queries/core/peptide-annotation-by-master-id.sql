select pept_master.annotation_id,
	(select cv.cv_name from nextprot.cv_quality_qualifiers cv where cv.cv_id=pept_master.cv_quality_qualifier_id) as quality_qualifier,
    pept_master.rank,
	iso.unique_name as iso_unique_name, 
	peptide.unique_name as pep_unique_name, 
	(mp.first_pos + 1) as first_pos, 
	mp.last_pos 
from nextprot.mapping_annotations pept_master, 
	nextprot.mapping_annotations pept_iso, 
	nextprot.mapping_annotations iso_master,
    nextprot.sequence_identifiers peptide, 
    nextprot.sequence_identifiers iso, 
    nextprot.mapping_positions mp,
    nextprot.identifier_properties prop 
where pept_master.mapped_identifier_id = peptide.identifier_id 
  and pept_iso.mapped_identifier_id = peptide.identifier_id
  and pept_master.cv_type_id = 10 
  and pept_iso.cv_type_id = 11
  and iso_master.cv_type_id = 4  
  and iso_master.reference_identifier_id = pept_master.reference_identifier_id  
  and iso_master.mapped_identifier_id = pept_iso.reference_identifier_id 
  and iso_master.mapped_identifier_id = iso.identifier_id 
  and mp.annotation_id = pept_iso.annotation_id 
  and pept_iso.rank=pept_master.rank
  and pept_master.cv_quality_qualifier_id in (10,50) -- GOLD and SILVER only
  and prop.identifier_id=peptide.identifier_id and prop.cv_property_name_id in (:propNameIds) and prop.property_value='Y'
  and pept_master.reference_identifier_id = :id
 

