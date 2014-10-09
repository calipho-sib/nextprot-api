select application_id, application_name, description, owner_id, usr.user_id as owner_id, usr.username as owner, organisation, responsible_email, responsible_name
from np_users.user_applications app
inner join np_users.users usr on (app.owner_id = usr.user_id)
where application_id =  :application_id
order by application_name;
