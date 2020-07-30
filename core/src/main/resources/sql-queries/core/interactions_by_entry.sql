select 
partnership_id as interaction_id, 
md5 as interaction_md5, 
qualityqualifier as interaction_quality,	
link_url as interaction_url,	
evidence_id,
evidence_datasource,
evidence_type,
evidence_quality,
evidence_xrefac,	
evidence_xrefdb,	
evidence_resource_id,
evidence_resource_type,	
number_of_experiments,
entry_name,	
unique_name as unique_name, 
entry_xref_id,
interactant_xref_id,
interactant_unique_name, 
is_interactant_in_nextprot, 
interactant_database,	
interactant_url, 
interactant_genename,
interactant_protname
	from (select 
		inter.partnership_id, inter.md5,
		(regexp_split_to_array(si.unique_name,'-'))[1]  as entry_name,
	       xr.accession as unique_name,
		ppa.db_xref_id as entry_xref_id,
		asp.db_xref_id as interactant_xref_id,
	       pdb.cv_name  as interactant_database,
	       pdb.url ||   pxr.accession as interactant_url
	       ,si.cv_type_id,
	(select cvq.cv_name 
	 from nextprot.cv_quality_qualifiers cvq 
	 where cvq.cv_id=inter.cv_quality_qualifier_id) as qualityQualifier,
	 pxr.accession as interactant_unique_name,
	 case when (psi.identifier_id is not null) then true else false end as is_interactant_in_nextprot, -- if an entry can be found in nextprot, take nextprot instead of uniprot
	 (select crx.accession
	from nextprot.partnership_resource_assoc pra 
	inner join nextprot.db_xrefs crx on (pra.resource_id=crx.resource_id) 
	where pra.partnership_id=inter.partnership_id
	) as evidence_xrefac,
	(select pra.resource_id
	from nextprot.partnership_resource_assoc pra 
	where pra.partnership_id=inter.partnership_id
	) as evidence_resource_id,
	(select rst.cv_name
	from nextprot.partnership_resource_assoc pra 
	inner join nextprot.resources rs on (pra.resource_id=rs.resource_id)
	inner join nextprot.cv_resource_types rst on (rs.cv_type_id=rst.cv_id)
	where pra.partnership_id=inter.partnership_id
	) as evidence_resource_type,
	(select cvpdbs.link_url
	from nextprot.partnership_resource_assoc pra 
	inner join nextprot.db_xrefs crx on (pra.resource_id=crx.resource_id) 
	inner join nextprot.cv_databases cvpdbs on (crx.cv_database_id=cvpdbs.cv_id)
	where pra.partnership_id=inter.partnership_id
	) as link_url,
	(select cvpdbs.cv_name
	from nextprot.partnership_resource_assoc pra 
	inner join nextprot.db_xrefs crx on (pra.resource_id=crx.resource_id) 
	inner join nextprot.cv_databases cvpdbs on (crx.cv_database_id=cvpdbs.cv_id)
	where pra.partnership_id=inter.partnership_id
	) as evidence_xrefdb,
	(select cv.cv_name
	from nextprot.partnership_resource_assoc pra 
	inner join nextprot.cv_qualifier_types cv on (pra.cv_eco_id=cv.cv_id) 
	where pra.partnership_id=inter.partnership_id
	) as evidence_type,
	(select pra.assoc_id from nextprot.partnership_resource_assoc pra 
	where pra.partnership_id=inter.partnership_id
	) as evidence_id,	
	(select cv.cv_name
	from nextprot.partnership_resource_assoc pra 
	inner join nextprot.cv_quality_qualifiers cv on (pra.cv_quality_qualifier_id=cv.cv_id) 
	where pra.partnership_id=inter.partnership_id
	) as evidence_quality,
	(select cv.cv_name from nextprot.partnership_resource_assoc pra 
	inner join nextprot.cv_datasources cv on (pra.assigned_by_id=cv.cv_id)
	where pra.partnership_id=inter.partnership_id
	) as evidence_datasource,
	(select pra_props.property_value
	from nextprot.partnership_resource_assoc pra 
	inner join nextprot.partnership_resource_assoc_properties pra_props on (pra_props.assoc_id = pra.assoc_id and pra_props.cv_property_name_id = 5) -- number of experiments
	where pra.partnership_id=inter.partnership_id
	) as number_of_experiments,
	(select rp.property_value from nextprot.resource_properties rp 
	where rp.resource_id=pxr.resource_id
	) as interactant_genename,
	(select psi.display_name) as interactant_protname
	from nextprot.sequence_identifiers si
	inner join nextprot.db_xrefs xr on (si.db_xref_id=xr.resource_id)
	inner join nextprot.cv_databases cvdbs on (xr.cv_database_id=cvdbs.cv_id)
	inner join nextprot.partnership_partner_assoc ppa on (xr.resource_id=ppa.db_xref_id)
	inner join nextprot.partnerships inter on (ppa.partnership_id=inter.partnership_id)
	left outer join nextprot.partnership_partner_assoc asp on (asp.partnership_id=ppa.partnership_id and asp.db_xref_id!=ppa.db_xref_id)
	left outer join nextprot.db_xrefs pxr on (asp.db_xref_id=pxr.resource_id)
	left outer join nextprot.cv_databases pdb on (pdb.cv_id=pxr.cv_database_id)
	left outer join nextprot.sequence_identifiers psi on (psi.db_xref_id=pxr.resource_id)
	where si.cv_status_id = 1 
	and si.cv_type_id in (1,2) 
	and inter.cv_status_id=1
	and inter.cv_quality_qualifier_id in (50,10) 
	and not (inter.is_xeno=false and psi.identifier_id is null and pxr.accession is not null)  -- removes interactions with partner which are human trEMBL proteins 
	) a
	where entry_name = :entryName
	order by interaction_quality, is_interactant_in_nextprot desc, interactant_unique_name

