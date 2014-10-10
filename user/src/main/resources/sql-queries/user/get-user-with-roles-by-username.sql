select u.username, u.name, u.user_id, r.role_name
from np_users.users u inner join np_users.user_roles r (u.user_id = r.user_id)
where username = :username;