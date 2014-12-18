DELETE FROM np_users.protein_list_items
WHERE list_id = :list_id
AND accession_number IN (:accession_numbers)