select advanced_user_query_id, title, description, sparql, public, submitted, username
from np_users.advanced_user_query q
inner join np_users.users u on (q.user_id = u.user_id)
where q.public = 'Y'
and u.username = 'nextprot'