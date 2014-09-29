CREATE TABLE np_users.user_applications(
  application_id BIGSERIAL PRIMARY KEY,
  application_name VARCHAR(100) NOT NULL,
  description VARCHAR(100) NOT NULL,
  organisation VARCHAR(100),
  responsible_name VARCHAR(100) NOT NULL,
  responsible_email VARCHAR(100) NOT NULL,
  website VARCHAR(100),
  owner VARCHAR(100) NOT NULL,
  token VARCHAR(1024) NOT NULL,
  state VARCHAR(10)
);

create unique index user_application_name_idx ON np_users.user_applications USING btree (owner, application_id);