SELECT query_id, title, description, sparql, published, public_id, user_name
FROM np_users.user_queries
LEFT JOIN np_users.users
ON owner_id = user_id
WHERE public_id = :public_id