SELECT query_id, title, description, sparql, published, user_name
FROM np_users.user_queries
INNER JOIN np_users.users users
ON owner_id = users.user_id
WHERE users.user_name = :user_name