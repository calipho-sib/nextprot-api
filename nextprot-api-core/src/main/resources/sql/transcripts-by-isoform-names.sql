select cvqq.cv_name quality, gene.identifier_id gene_id, gene.unique_name gene_name, isoforms.unique_name isoform, transcript.unique_name transcript, xrefs.accession accession, dbs.cv_name database_name, ens_protein_xref.accession ensemble_protein, bio_seq.bio_sequence bio_sequence    
from nextprot.sequence_identifiers isoforms      
inner join nextprot.mapping_annotations mapping on (mapping.mapped_identifier_id = isoforms.identifier_id)      
inner join nextprot.cv_mapping_annotation_types mapping_types on (mapping.cv_type_id = mapping_types.cv_id)      
inner join nextprot.sequence_identifiers transcript on (transcript.identifier_id = mapping.reference_identifier_id)      
inner join nextprot.mapping_annotations mapping_gene on (mapping_gene.mapped_identifier_id = transcript.identifier_id and mapping_gene.cv_type_id = 2)  
inner join nextprot.sequence_identifiers gene on (gene.identifier_id = mapping_gene.reference_identifier_id)      
inner join nextprot.bio_sequences bio_seq on (bio_seq.identifier_id = transcript.identifier_id)      
inner join nextprot.identifier_resource_assoc assoc_ens_protein on (assoc_ens_protein.identifier_id = transcript.identifier_id)  
left join nextprot.resources ens_protein on (ens_protein.resource_id = assoc_ens_protein.resource_id)   
inner join nextprot.db_xrefs ens_protein_xref on (ens_protein_xref.resource_id = ens_protein.resource_id)   
inner join nextprot.db_xrefs xrefs on (xrefs.resource_id = transcript.db_xref_id)   
inner join nextprot.cv_databases dbs on (xrefs.cv_database_id = dbs.cv_id)   
inner join nextprot.cv_quality_qualifiers cvqq on (cvqq.cv_id = mapping.cv_quality_qualifier_id)   
where isoforms.unique_name in (:isoform_names)   
and mapping_types.cv_name = 'PROTEIN_ISOFORM_TRANSCRIPT'  
and ens_protein_xref.cv_database_id = dbs.cv_id
				