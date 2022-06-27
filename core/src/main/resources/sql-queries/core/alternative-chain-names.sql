select 'chain'::text as category, si.unique_name, 'cleavedRegionNames'::text as name_class, 
ct.cv_name as name_type, 
        CASE  WHEN ct.cv_name='enzyme name' THEN 'EC'  
  	   ELSE qt.cv_name END as name_qualifier, 
  	   asy.is_main, 
	   CASE  WHEN ct.cv_name='enzyme name' THEN substr(asy.synonym_name,4)  -- remove the 'EC ' and keep just the enzyme code
  	   ELSE asy.synonym_name END as synonym_name, 
  	   ('MP_' ||asy.synonym_id)::text as synonym_id, ('MP_' || asy.parent_id)::text as parent_id
from nextprot.annotation_synonyms asy
inner join nextprot.annotations an on asy.annotation_id=an.annotation_id
inner join nextprot.cv_synonym_types ct on asy.cv_type_id=ct.cv_id
inner join nextprot.cv_terms at on an.cv_annotation_type_id=at.cv_id
inner join nextprot.sequence_identifiers si on si.identifier_id=an.identifier_id
left outer join nextprot.cv_synonym_qualifiers qt on (qt.cv_id=asy.cv_qualifier_id)
where at.cv_name='mature protein' 
and parent_id is not null
and si.unique_name = :uniqueName
order by unique_name