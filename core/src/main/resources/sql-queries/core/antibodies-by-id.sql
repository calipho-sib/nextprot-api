select
  antibody_master.reference_identifier_id as master_id,
  antibody_master.annotation_id as annotation_id,
  iso.unique_name as iso_unique_name,
  antibody.unique_name as antibody_unique_name,
  antibody.db_xref_id as db_xref_id,
  src.cv_name as antibody_src,
  (mp.first_pos + 1) as first_pos,
  mp.last_pos,
  xr.accession as resource_ac,
  db.cv_name as resource_db
from nextprot.mapping_annotations antibody_master,
  nextprot.mapping_annotations antibody_iso,
  nextprot.mapping_annotations iso_master,
  nextprot.sequence_identifiers antibody,
  nextprot.sequence_identifiers iso,
  nextprot.mapping_positions mp,
  nextprot.cv_datasources src,
  nextprot.cv_databases db,
  nextprot.db_xrefs xr
where antibody_master.mapped_identifier_id = antibody.identifier_id
  and antibody_master.cv_type_id = 5
  and antibody_iso.mapped_identifier_id = antibody.identifier_id
  and antibody_iso.cv_type_id = 6
  and iso_master.cv_type_id = 4
  and iso_master.reference_identifier_id = antibody_master.reference_identifier_id
  and iso_master.mapped_identifier_id = antibody_iso.reference_identifier_id
  and iso_master.mapped_identifier_id = iso.identifier_id
  and mp.annotation_id = antibody_iso.annotation_id
  and src.cv_id=antibody.datasource_id
  and xr.resource_id = antibody.db_xref_id
  and db.cv_id = xr.cv_database_id
  and antibody_master.reference_identifier_id = :id