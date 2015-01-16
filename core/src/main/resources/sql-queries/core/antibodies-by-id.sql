select 
iso.unique_name as iso_unique_name, 
antibody.unique_name as antibody_unique_name, 
antibody.db_xref_id as db_xref_id,
src.cv_name as antibody_src,
mp.first_pos, 
mp.last_pos
from nextprot.mapping_annotations anti_master,  
nextprot.mapping_annotations anti_iso,  
nextprot.mapping_annotations iso_master,   
nextprot.sequence_identifiers antibody,  
nextprot.sequence_identifiers iso,  
nextprot.mapping_positions mp,
nextprot.cv_datasources src  
where anti_master.mapped_identifier_id = antibody.identifier_id   
and anti_master.cv_type_id = 5   
and anti_iso.mapped_identifier_id = antibody.identifier_id   
and anti_iso.cv_type_id = 6   
and iso_master.cv_type_id = 4   
and iso_master.reference_identifier_id = anti_master.reference_identifier_id   
and iso_master.mapped_identifier_id = anti_iso.reference_identifier_id   
and iso_master.mapped_identifier_id = iso.identifier_id   
and mp.annotation_id = anti_iso.annotation_id  
and src.cv_id=antibody.datasource_id
and anti_master.reference_identifier_id = :id