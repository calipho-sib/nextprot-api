SELECT list_id, list_name, description, owner_id, user_name, public_id, 0 as protCount
FROM np_users.user_protein_lists
LEFT JOIN np_users.users ON owner_id = user_id
WHERE public_id = :public_id
