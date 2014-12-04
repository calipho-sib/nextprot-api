SELECT tag_name, query_id
FROM np_users.user_query_tags
WHERE query_id in (:query_ids)