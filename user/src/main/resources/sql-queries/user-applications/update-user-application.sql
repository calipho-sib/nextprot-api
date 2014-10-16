UPDATE np_users.user_applications
SET (application_name, description, organisation, responsible_email, responsible_name, website, token, status, user_data_access, origins) =
  (:application_name, :description, :organisation, :responsible_email, :responsible_name, :website, :token, :status, :user_data_access, :origins)
WHERE application_id = :application_id
