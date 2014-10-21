INSERT into np_users.user_applications (application_name, description, organisation, responsible_name, responsible_email,
  website, owner_id, token, status, user_data_access, origins
)
VALUES (:application_name, :description, :organisation, :responsible_name, :responsible_email,
  :website, :owner_id, :token, :status, :user_data_access, :origins)