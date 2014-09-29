CREATE TABLE np_users.user_queries (
  user_query_id bigserial PRIMARY KEY,
  title character varying(256) NOT NULL,
  description character varying(1000),
  sparql character varying(1000) NOT NULL,
  public character varying(1) NOT NULL DEFAULT 'N',
  submitted character varying(1) NOT NULL DEFAULT 'N',
  owner_id bigint references np_users.users(user_id) REFERENCES np_users.users (user_id)
);

CREATE UNIQUE INDEX user_query_title_ux  ON np_users.user_queries (title, owner_id);

CREATE INDEX user_query_public_idx  ON np_users.user_queries (public);

CREATE INDEX user_query_submitted_idx  ON np_users.user_queries (submitted);

CREATE INDEX user_query_title_idx  ON np_users.user_queries (title);

