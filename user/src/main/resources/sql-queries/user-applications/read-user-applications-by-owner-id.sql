SELECT application_id, application_name, description, owner_id, users.username AS owner, organisation, responsible_email, responsible_name
FROM np_users.user_applications apps
INNER JOIN np_users.users users ON (apps.owner_id = users.user_id)
WHERE owner_id = :owner_id
ORDER BY application_name;