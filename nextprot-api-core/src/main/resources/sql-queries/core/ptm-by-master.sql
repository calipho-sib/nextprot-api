select distinct t1.unique_name, t1.type, t1.accession, t1.cv_name, t1.quality, t1.description, 
       t1.first_pos, t1.last_pos, t2.cv_qualifier_type_id
  from (
       select distinct a.annotation_id, si.unique_name, type.cv_name as type,xr.accession,  
              cv.cv_name, a.cv_quality_qualifier_id as quality, a.description, 
              pfp.first_pos, pfp.last_pos
          from nextprot.sequence_identifiers sim, nextprot.annotations a
               left outer join nextprot.cv_terms cv on (a.cv_term_id = cv.cv_id)
               left outer join nextprot.db_xrefs xr on (cv.db_xref_id = xr.resource_id),
               nextprot.cv_terms type, nextprot.annotation_protein_assoc apa, 
               nextprot.protein_feature_positions pfp, nextprot.sequence_identifiers si
         where sim.unique_name = :uniqueName
           and sim.identifier_id = a.identifier_id
           and a.cv_annotation_discriminator_id = 13
           and a.cv_annotation_type_id = type.cv_id
           and a.annotation_id = apa.annotation_id
           and apa.protein_id = si.identifier_id
           and si.cv_type_id = 2
           and apa.assoc_id = pfp.annotation_protein_id
       ) as t1,
       (
       select distinct a.annotation_id,  max(ara.cv_qualifier_type_id) as cv_qualifier_type_id
         from nextprot.sequence_identifiers si, nextprot.annotations a,
              nextprot.annotation_resource_assoc ara
        where si.unique_name = :uniqueName
          and si.identifier_id = a.identifier_id
          and a.cv_annotation_discriminator_id = 13
          and a.annotation_id = ara.annotation_id
        group by  a.annotation_id
        ) as t2
  where t1.annotation_id = t2.annotation_id
  order by t1.unique_name, t1.first_pos	