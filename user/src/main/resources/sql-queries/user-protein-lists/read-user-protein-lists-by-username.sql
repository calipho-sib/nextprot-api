SELECT lists.list_id, list_name, description, owner_id, user_name, count(proteins.accession_number) as protCount
FROM np_users.user_protein_lists lists
INNER JOIN np_users.protein_list_items proteins ON lists.list_id = proteins.list_id
INNER JOIN np_users.users users ON users.user_id = lists.owner_id
WHERE users.user_name = :user_name
GROUP BY lists.list_id, user_name
ORDER BY list_name;