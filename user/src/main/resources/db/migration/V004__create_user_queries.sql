CREATE TABLE np_users.user_queries (
  query_id bigserial PRIMARY KEY,
  title CHARACTER VARYING(256) NOT NULL,
  description CHARACTER VARYING(1000),
  sparql CHARACTER VARYING(1000) NOT NULL,
  published CHARACTER VARYING(1) NOT NULL DEFAULT 'N',
  owner_id bigint REFERENCES np_users.users(user_id) ON DELETE SET NULL
);

CREATE UNIQUE INDEX user_query_title_ux  ON np_users.user_queries (title, owner_id);

CREATE INDEX user_query_published_idx  ON np_users.user_queries (published);

CREATE INDEX user_query_title_idx  ON np_users.user_queries (title);

CREATE TABLE np_users.user_query_tags (
  tag_name CHARACTER VARYING(256) NOT NULL,
  query_id bigint REFERENCES np_users.user_queries(query_id) ON DELETE SET NULL,

  CONSTRAINT user_query_pk PRIMARY KEY (query_id, tag_name)
);

