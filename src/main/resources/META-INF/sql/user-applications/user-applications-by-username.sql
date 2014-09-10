select application_id, application_name
from np_users.user_applications
where application_owner =  :owner
order by application_name;
