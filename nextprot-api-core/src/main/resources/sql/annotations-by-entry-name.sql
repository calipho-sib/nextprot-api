select a.unique_name annotation_unique_name, a.annotation_id annotation_id,   t.cv_name category,   a.description description,   q.cv_name quality_qualifier,   t2.cv_name cv_term_name, term_type.cv_name cv_term_type,
				 x.accession cv_term_accession,   v.variant_sequence variant_sequence,   v.original_sequence original_sequence  , syn.synonym_name synonym
				 from nextprot.annotations a 
				 inner join nextprot.sequence_identifiers s on (s.identifier_id = a.identifier_id)  
				 left join nextprot.cv_terms t on (a.cv_annotation_type_id = t.cv_id)  
				 inner join nextprot.cv_term_categories term_type on (term_type.cv_id=t.cv_category_id)
				 left join nextprot.cv_quality_qualifiers q on (q.cv_id = a.cv_quality_qualifier_id) 
				 left join nextprot.cv_terms t2 on (a.cv_term_id = t2.cv_id) 
				 left join nextprot.db_xrefs x on (t2.db_xref_id = x.resource_id)  
				 left join nextprot.variants v on (v.annotation_id = a.annotation_id) 
 				 left join nextprot.annotation_synonyms syn on (syn.annotation_id = a.annotation_id and syn.cv_type_id = 10) 
				 where t.cv_name not in ('enzyme classification', 'uniprot keyword', 'family name') 
				 and s.unique_name = :unique_name