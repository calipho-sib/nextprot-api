DELETE FROM np_users.protein_list_items
WHERE list_id = :list_id
and accession_number in (:accession_numbers)