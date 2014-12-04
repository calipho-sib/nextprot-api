SELECT query_id, title, description, sparql, published, user_name
FROM np_users.user_queries
LEFT JOIN np_users.users
ON owner_id = user_id
WHERE published = 'Y'
AND user_name != 'nextprot'