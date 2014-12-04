DELETE FROM np_users.user_query_tags
WHERE tag_name
IN (:tags)