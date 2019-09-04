select synonym_name from nextprot.identifier_synonyms
join nextprot.gene_identifiers
on gene_identifiers.identifier_id = identifier_synonyms.identifier_id
where chromosome= (:chromosome) and first_pos_chr = (:first_position) and last_pos_chr = (:last_position) and cv_type_id = 1