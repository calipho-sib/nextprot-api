select application_id, application_name, description, owner, organisation, responsible_email, responsible_name
from np_users.user_applications
where owner =  :owner
order by application_name;
