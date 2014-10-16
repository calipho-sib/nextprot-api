CREATE TABLE np_users.user_applications(
  application_id BIGSERIAL PRIMARY KEY,
  application_name VARCHAR(100) NOT NULL,
  description VARCHAR(100) NOT NULL,
  organisation VARCHAR(100),
  responsible_name VARCHAR(100),
  responsible_email VARCHAR(100),
  website VARCHAR(100),
  owner_id bigint references np_users.users(user_id),
  token VARCHAR(1024) NOT NULL, -- api id
  status VARCHAR(10) NOT NULL, --active, banned
  user_data_access VARCHAR (2) NOT NULL default 'RO' CHECK ((user_data_access == 'RO') OR (user_data_access == 'RW')),
  origins varchar(512), -- hostname hosting the webapp, used to make sure the call to the API is performed from that origin
  -- last_session_date TIMESTAMP,
  creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create unique index user_application_owner_app_udx ON np_users.user_applications USING btree (owner_id, application_name);
