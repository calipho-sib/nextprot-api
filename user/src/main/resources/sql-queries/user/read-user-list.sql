SELECT users.user_id, user_name, first_name, last_name, role_name
FROM np_users.users users
LEFT JOIN np_users.user_roles roles ON (users.user_id = roles.user_id)
ORDER BY user_name