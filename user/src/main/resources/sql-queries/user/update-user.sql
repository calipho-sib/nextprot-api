UPDATE np_users.users
SET (user_name, first_name, last_name) = (:user_name, :first_name, :last_name)
WHERE user_id = :user_id;