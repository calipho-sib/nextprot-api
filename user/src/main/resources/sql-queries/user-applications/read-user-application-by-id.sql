SELECT application_id, application_name, description, owner_id, users.user_name AS owner, organisation, responsible_email, responsible_name,
  organisation, website, token, status, user_data_access, origins, creation_date
FROM np_users.user_applications apps
INNER JOIN np_users.users users ON (apps.owner_id = users.user_id)
WHERE application_id = :application_id
ORDER BY application_name;
