select  
cvqq.cv_name as quality, 
gen.identifier_id as gene_id, 
gen.unique_name as gene_name, 
iso.unique_name as isoform, 
tra.unique_name as transcript, 
enstx.accession as enst_ac, 
enspx.accession as ensp_ac, 
tra_seq.bio_sequence as tr_sequence    
from nextprot.sequence_identifiers iso
inner join nextprot.mapping_annotations itmap on (itmap.mapped_identifier_id = iso.identifier_id and itmap.cv_type_id=7) -- 7:iso-tra map     
inner join nextprot.sequence_identifiers tra on (tra.identifier_id = itmap.reference_identifier_id)      
inner join nextprot.mapping_annotations tgmap on (tgmap.mapped_identifier_id = tra.identifier_id and tgmap.cv_type_id = 2)  
inner join nextprot.sequence_identifiers gen on (gen.identifier_id = tgmap.reference_identifier_id)      
inner join nextprot.bio_sequences tra_seq on (tra_seq.identifier_id = tra.identifier_id)      
inner join nextprot.cv_quality_qualifiers cvqq on (cvqq.cv_id = itmap.cv_quality_qualifier_id)   
inner join nextprot.db_xrefs enstx on (enstx.resource_id = tra.db_xref_id and enstx.cv_database_id = 32)   
left outer join (
  select tr_ira.identifier_id, px.accession
  from nextprot.identifier_resource_assoc tr_ira
  inner join nextprot.resources rp on (rp.resource_id = tr_ira.resource_id)   
  inner join nextprot.db_xrefs px 
    on (px.resource_id = rp.resource_id and px.cv_database_id = 32)   
  ) as enspx on (enspx.identifier_id = tra.identifier_id)
where iso.unique_name in (:isoform_names) 
