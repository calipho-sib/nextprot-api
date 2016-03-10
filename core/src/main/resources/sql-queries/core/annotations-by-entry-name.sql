select 
a.unique_name annotation_unique_name, 
a.annotation_id annotation_id,   
cvt.cv_name category,
a.description description,   
q.cv_name quality_qualifier, 
cvt2.cv_id cv_term_id,
cvt2.cv_name cv_term_name,
cvt2.description as cv_term_description,
(select tp.property_value from nextprot.cv_term_properties tp where tp.cv_term_id=cvt2.cv_id and tp.property_name = 'Category') as cv_term_type,
dbx.accession cv_term_accession,
v.variant_sequence variant_sequence,   
v.original_sequence original_sequence,
syn.synonym_name synonym,
(select string_agg(allsyn.synonym_name,'|') from nextprot.annotation_synonyms allsyn where allsyn.annotation_id = a.annotation_id ) as synonyms,
cvtc.cv_api_name cv_api_name
from nextprot.annotations a 
inner join nextprot.sequence_identifiers si on (si.identifier_id = a.identifier_id)
left join nextprot.cv_terms cvt on (a.cv_annotation_type_id = cvt.cv_id)
inner join nextprot.cv_term_categories term_type on (term_type.cv_id=cvt.cv_category_id)
left join nextprot.cv_quality_qualifiers q on (q.cv_id = a.cv_quality_qualifier_id)
left join nextprot.cv_terms cvt2 on (a.cv_term_id = cvt2.cv_id)
left join nextprot.cv_term_categories cvtc on (cvtc.cv_id=cvt2.cv_category_id)
left join nextprot.db_xrefs dbx on (cvt2.db_xref_id = dbx.resource_id)
left join nextprot.variants v on (v.annotation_id = a.annotation_id) 
left join nextprot.annotation_synonyms syn on (syn.annotation_id = a.annotation_id and syn.cv_type_id = 10) 
-- family name is handled elsewhere
-- alignment confilict is a "technical annotation" to be ignored
where cvt.cv_name not in ( 'family name', 'alignment conflict')
and si.unique_name = :unique_name
