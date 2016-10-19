select sim.unique_name, isy.synonym_name as gene_name
from nextprot.sequence_identifiers sim
     inner join nextprot.identifier_synonyms isy on (isy.identifier_id = sim.identifier_id and isy.cv_type_id = 100)
where sim.cv_type_id = 1
  and sim.cv_status_id = 1
  and isy.is_main = true
order by sim.unique_name