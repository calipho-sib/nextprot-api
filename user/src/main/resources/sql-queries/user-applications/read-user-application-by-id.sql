select application_id, application_name, description, owner_id, users.username as owner, organisation, responsible_email, responsible_name
from np_users.user_applications apps
inner join np_users.users users on (apps.owner_id = users.user_id)
where application_id = :application_id
order by application_name;
