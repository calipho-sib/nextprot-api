select distinct 
peptide.identifier_id as peptide_id, peptide.unique_name as peptide_name, 
prop.identifier_property_id as property_id, prop.cv_property_name_id as prop_name_id,
pn.cv_name as prop_name, prop.property_value as prop_value
from nextprot.sequence_identifiers peptide
inner join nextprot.identifier_properties prop on (peptide.identifier_id=prop.identifier_id)
inner join nextprot.cv_property_names pn on (prop.cv_property_name_id=pn.cv_id)
where peptide.unique_name in (:names)
