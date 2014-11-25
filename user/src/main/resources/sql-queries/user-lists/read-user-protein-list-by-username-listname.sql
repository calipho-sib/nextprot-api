SELECT list_id, list_name, description, owner_id, user_name, 0 as protCount
FROM np_users.user_protein_lists
LEFT JOIN np_users.users ON user_id = owner_id
WHERE user_name = :user_name
AND list_name = :list_name