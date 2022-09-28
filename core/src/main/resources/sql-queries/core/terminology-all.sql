   select distinct 
          nextprot.cv_terms.cv_id as id, 
          nextprot.db_xrefs.accession as accession, 
(select selfcat.cv_name || '^ ' || selfdb.cv_name || '^ ' || nextprot.db_xrefs.accession || '^ ' || nextprot.db_xrefs.resource_id || '^ ' || selfdb.url || '^ ' || selfdb.link_url
from nextprot.cv_databases selfdb
inner join nextprot.cv_database_categories selfcat on (selfdb.cv_category_id=selfcat.cv_id) 
where nextprot.db_xrefs.cv_database_id=selfdb.cv_id
) as selfxref,       
          nextprot.cv_terms.cv_name as name, 
          nextprot.cv_terms.description as description, 
          nextprot.cv_term_categories.cv_api_name as ontology, 
          nextprot.cv_term_categories.cv_name as ontologyAltname,
          nextprot.cv_term_categories.cv_display_name as ontologyDisplayName,
          (select string_agg(cvsyn.synonym_name, ' | ') from  nextprot.cv_term_synonyms cvsyn where cvsyn.is_main is false and cvsyn.cv_term_id=nextprot.cv_terms.cv_id ) as synonyms,
          (select string_agg('abbreviation:=' || cvsyn.synonym_name, ' | ') from  nextprot.cv_term_synonyms cvsyn where cvsyn.is_main is true and cvsyn.cv_term_id=nextprot.cv_terms.cv_id ) as abbreviations,
          (select string_agg(properties.property_name ||':='|| properties.property_value, ' | ') from nextprot.cv_term_properties properties where properties.cv_term_id = nextprot.cv_terms.cv_id) as properties,
-- get ancestor          
          (select string_agg((select (xref.accession || '->' || root_rt.cv_name) from nextprot.db_xrefs   xref where parent.db_xref_id=xref.resource_id ),'|')
      from nextprot.cv_terms parent
inner join nextprot.cv_term_relationships root_r on (parent.cv_id=root_r.object_id)
inner join nextprot.cv_term_relationship_types root_rt on (root_r.cv_type_id = root_rt.cv_id)
inner join nextprot.cv_terms root on (root_r.subject_id=root.cv_id)
inner join nextprot.db_xrefs xrefr on (root.db_xref_id=xrefr.resource_id) 
     where xrefr.accession=nextprot.db_xrefs.accession  and parent.cv_status_id=1)  as ancestor, 
-- get children
  (select string_agg(cx.accession || '->' || rt.cv_name,'|')
    from nextprot.cv_term_relationships r 
    inner join nextprot.cv_terms child on (child.cv_id=r.subject_id)
    inner join nextprot.cv_term_relationship_types rt on (r.cv_type_id = rt.cv_id)
    inner join nextprot.db_xrefs cx on (child.db_xref_id=cx.resource_id)
    where nextprot.cv_terms.cv_id=r.object_id and child.cv_status_id=1
  ) as children,
-- get xrefs
	(select string_agg(cat || '^ ' || db  || '^ ' || ac  || '^ ' ||  xref_id || '^ ' ||  url  || '^ ' || link_url  || '^ ' || term_id || '^ ' || term_name || '^ ' || term_onto, ' | ') from (
	select dbc2.cv_name as cat, db2.cv_name as db, x2.accession as ac, x2.resource_id as xref_id, db2.url, db2.link_url, coalesce(t2.cv_id,-1) as term_id
	, coalesce(t2.cv_name,'') as term_name, coalesce(tc2.cv_display_name,'') as term_onto
	from 
	nextprot.cv_term_db_xref_assoc txa 
	inner join nextprot.db_xrefs x2 on (txa.db_xref_id=x2.resource_id)
	inner join nextprot.cv_databases db2 on (x2.cv_database_id=db2.cv_id)
	inner join nextprot.cv_database_categories dbc2 on (db2.cv_category_id=dbc2.cv_id)
	left join nextprot.cv_terms t2 on (x2.resource_id=t2.db_xref_id and t2.cv_status_id = 1)
	left join nextprot.cv_term_categories tc2 on (t2.cv_category_id=tc2.cv_id)
	where txa.cv_term_id=cv_terms.cv_id
	union
	select dbc2.cv_name as cat, db2.cv_name as db, x2.accession as ac, x2.resource_id as xref_id, db2.url, db2.link_url, coalesce(t2.cv_id,-1) as term_id
	, coalesce(t2.cv_name,'') as term_name, coalesce(tc2.cv_display_name,'') as term_onto
	from nextprot.cv_term_db_xref_assoc txa 
	inner join nextprot.cv_terms t2 on (txa.cv_term_id=t2.cv_id and t2.cv_status_id = 1)
	inner join nextprot.cv_term_categories tc2 on (t2.cv_category_id=tc2.cv_id)
	inner join nextprot.db_xrefs x2 on (t2.db_xref_id=x2.resource_id)
	inner join nextprot.cv_databases db2 on (x2.cv_database_id=db2.cv_id)
	inner join nextprot.cv_database_categories dbc2 on (db2.cv_category_id=dbc2.cv_id)
	where txa.db_xref_id=cv_terms.db_xref_id
	) a ) as xref
from nextprot.cv_terms
inner join nextprot.db_xrefs on (nextprot.cv_terms.db_xref_id = nextprot.db_xrefs.resource_id)
inner join nextprot.cv_term_categories on (nextprot.cv_terms.cv_category_id = nextprot.cv_term_categories.cv_id)
where  nextprot.cv_terms.cv_status_id=1 -- We don't take obsolete terms
and nextprot.cv_term_categories.cv_api_name != 'UniprotFamilyCv' -- we don't take Uniprot Family terms
order by nextprot.cv_term_categories.cv_api_name
  