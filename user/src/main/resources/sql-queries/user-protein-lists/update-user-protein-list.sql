UPDATE np_users.user_protein_lists
SET (list_name, description) = (:list_name, :description)
WHERE list_id = :list_id