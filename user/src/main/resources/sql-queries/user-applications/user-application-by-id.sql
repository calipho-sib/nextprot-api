select application_id, application_name, description, owner, organisation, responsible_email, responsible_name
from np_users.user_applications
where application_id =  :application_id
order by application_name;
