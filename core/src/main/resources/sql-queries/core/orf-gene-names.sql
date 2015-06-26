-- adding orf gene names, which were not part of the view_master_identifer_names
select 'geneNames'::text as name_class, 'ORFName'::text as category, 'gene name'::text as name_type, null::text as name_qualifier,
	   sy.is_main, sy.synonym_name, sy.synonym_name, 'PR_' || sy.synonym_id as synonym_id, 'PR_' || sy.parent_id as parent_id 
from nextprot.identifier_synonyms sy
inner join nextprot.sequence_identifiers si on (si.identifier_id = sy.identifier_id)
inner join nextprot.cv_synonym_types st on (sy.cv_type_id = st.cv_id)
where si.unique_name = :uniqueName
and cv_name = 'open reading frame'
order by unique_name