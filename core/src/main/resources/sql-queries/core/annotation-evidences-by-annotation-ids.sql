select  cvcat.cv_api_name as ontology,
        assoc.is_negative_evidence,
        assoc.annotation_id,
        assoc.experimental_context_id,
        q.cv_name as quality_qualifier,
        assoc.resource_id, 
        assoc.assoc_id as evidence_id, 
        cva.cv_name as resource_assoc_type, 
        cvq.cv_name as qualifier_type, 
        cvres.cv_name as resource_type, 
        dbxrefs.accession as resource_accession,
        cv_db.cv_name as resource_db,
        cv_db.description as resource_desc,
        pub.md5 as publication_md5,
        cv_src.cv_name as evidence_assigned_by,
-- when assigned by 9 evidence is computed else evidence is curated        
        (case when assoc.assigned_by_id=9 then 'computed' else 'curated' end) as assignment_method,
        eco.cv_name as eco_name,
        ecoxr.accession as eco_ac
from nextprot.annotation_resource_assoc assoc     
left join nextprot.publications pub on (pub.resource_id = assoc.resource_id)  
left join nextprot.resources res on (res.resource_id = assoc.resource_id)  
left join nextprot.cv_resource_assoc_types cva on (cva.cv_id = assoc.cv_type_id)   
left join nextprot.cv_qualifier_types cvq on (cvq.cv_id = assoc.cv_qualifier_type_id)  
left join nextprot.cv_resource_types cvres on (res.cv_type_id = cvres.cv_id)  
left join nextprot.db_xrefs dbxrefs on (dbxrefs.resource_id = res.resource_id) 
left join nextprot.cv_datasources cv_src on (assoc.assigned_by_id = cv_src.cv_id) 
left join nextprot.cv_databases cv_db on (dbxrefs.cv_database_id = cv_db.cv_id) 
left join nextprot.cv_quality_qualifiers q on (q.cv_id = assoc.cv_quality_qualifier_id) 
left join nextprot.cv_terms eco on (assoc.evidence_code_id=eco.cv_id)
left join nextprot.db_xrefs ecoxr on (eco.db_xref_id=ecoxr.resource_id)
left join nextprot.cv_term_categories cvcat on (eco.cv_category_id = cvcat.cv_id)
-- evidence with type 1 (source) are now ignored (Anne)
-- evidence with type 2 (relative) exist for cofactor, sequence caution, and disease annotation types:
-- they are turned into properties and to build the description of the annotation, see CALIPHOMISC-277
where assoc.cv_type_id in (3,2)
and assoc.annotation_id in (:ids) 


