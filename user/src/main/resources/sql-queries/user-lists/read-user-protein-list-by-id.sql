SELECT list_id, list_name, description, owner_id, user_name, 0 as protCount
FROM np_users.user_protein_lists
LEFT JOIN np_users.users ON owner_id = user_id
WHERE list_id = :list_id
