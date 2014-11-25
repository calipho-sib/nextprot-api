SELECT queries.query_id, title, description, sparql, published, user_name
FROM np_users.user_queries queries
  INNER JOIN np_users.users users
    ON owner_id = users.user_id
  INNER JOIN np_users.user_query_tags tags
    ON queries.query_id = tags.query_id
WHERE tag_name = :tag_name