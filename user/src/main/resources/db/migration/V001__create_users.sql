CREATE TABLE np_users.users (
  user_id BIGSERIAL PRIMARY KEY,
  user_name VARCHAR(256) NOT NULL,
  first_name VARCHAR(256),
  last_name VARCHAR(256)
);

CREATE UNIQUE index users_username_udx ON np_users.users USING btree (user_name);

CREATE TABLE np_users.user_roles (
  role_name VARCHAR(256) NOT NULL CHECK (role_name IN ('ROLE_ADMIN', 'ROLE_USER', 'ROLE_APP')),
  user_id bigint REFERENCES np_users.users(user_id) ON DELETE CASCADE,
  CONSTRAINT user_role_pk PRIMARY KEY (role_name, user_id)
);

insert into np_users.users (user_name, first_name, last_name) values ('ddtxra@gmail.com', 'Daniel', 'Teixeira');
insert into np_users.user_roles (role_name, user_id) values ('ROLE_USER', 1);
insert into np_users.user_roles (role_name, user_id) values ('ROLE_ADMIN', 1);
