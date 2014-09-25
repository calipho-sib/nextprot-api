CREATE TABLE np_users.user_lists
(
  list_id bigserial primary key,
  name character varying(200),
  description character varying(2000),
  owner_id bigint references np_users.users(user_id),
  CONSTRAINT list_u UNIQUE (name, owner_id)
);

create unique index user_lists_name_udx ON np_users.user_lists USING btree (name, owner_id);

CREATE TABLE np_users.list_proteins
(
  list_id integer NOT NULL references np_users.user_lists (list_id),
  entry character varying(10) NOT NULL,
  CONSTRAINT list_proteins_pkey PRIMARY KEY (list_id, entry)
);
