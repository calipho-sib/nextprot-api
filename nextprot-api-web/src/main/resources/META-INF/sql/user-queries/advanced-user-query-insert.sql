INSERT into np_users.advanced_user_query (title, description, sparql, user_id, public) 
VALUES (?, ?, ?, (select user_id from np_users.users where username = ?), 'N')