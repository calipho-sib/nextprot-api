CREATE TABLE np_users.users (
  user_id BIGSERIAL PRIMARY KEY,
  user_name VARCHAR(256) NOT NULL,
  first_name VARCHAR(256),
  last_name VARCHAR(256)
);

create unique index users_username_udx ON np_users.users USING btree (user_name);

CREATE TABLE np_users.user_roles (
  role_name VARCHAR(256) NOT NULL CHECK (role_name IN ('ADMIN', 'USER', 'APP')),
  user_id bigint references np_users.users(user_id),
  CONSTRAINT user_role_pk PRIMARY KEY (role_name, user_id)
);

