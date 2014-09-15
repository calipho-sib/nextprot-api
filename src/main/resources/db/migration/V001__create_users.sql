CREATE TABLE np_users.users (
  user_id BIGSERIAL PRIMARY KEY,
  username VARCHAR(256) NOT NULL
);

create unique index users_username ON np_users.users USING btree (username);