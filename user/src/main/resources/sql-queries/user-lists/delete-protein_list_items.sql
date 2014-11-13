DELETE FROM np_users.protein_list_items
WHERE accession_number
IN (:accession_numbers)