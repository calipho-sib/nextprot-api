CREATE TABLE np_users.user_protein_lists
(
  list_id BIGSERIAL PRIMARY KEY,
  list_name CHARACTER VARYING(200),
  description CHARACTER VARYING(2000),
  owner_id bigint REFERENCES np_users.users(user_id) ON DELETE SET NULL,
  CONSTRAINT list_u UNIQUE (list_name, owner_id)
);

CREATE UNIQUE index user_protein_lists_name_udx ON np_users.user_protein_lists USING btree (list_name, owner_id);

CREATE TABLE np_users.protein_list_items
(
  accession_number CHARACTER VARYING(10) NOT NULL,
  list_id INTEGER NOT NULL REFERENCES np_users.user_protein_lists(list_id) ON DELETE CASCADE,

  CONSTRAINT protein_pk PRIMARY KEY (list_id, accession_number)
);
