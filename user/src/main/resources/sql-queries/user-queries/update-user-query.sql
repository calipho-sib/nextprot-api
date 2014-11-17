UPDATE np_users.user_queries
SET (title, description, sparql, published) = (:title, :description, :sparql, :published)
WHERE query_id = :query_id