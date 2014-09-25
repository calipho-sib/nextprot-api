/**********************************/
/* Table Name: np_users.advanced_user_query */
/**********************************/
CREATE TABLE np_users.advanced_user_query(
  advanced_user_query_id BIGSERIAL NOT NULL,
  title varchar(50) NOT NULL,
  description varchar(1000),
  sparql varchar(1000) NOT NULL,
  public varchar(1) NOT NULL DEFAULT 'N',
  submitted varchar(1) NOT NULL DEFAULT 'N', --can be N(Not submitted), W(wait for approval), A(approved) when not approved go back to N
  user_id BIGINT
);

ALTER TABLE np_users.advanced_user_query ADD CONSTRAINT PK_advanced_user_query PRIMARY KEY (advanced_user_query_id);

-- imported key
ALTER TABLE np_users.advanced_user_query ADD CONSTRAINT fk1_advanced_user_query_user FOREIGN KEY (user_id) REFERENCES np_users.users (user_id);

-- indexes
CREATE INDEX idx_advanced_user_query_title ON np_users.advanced_user_query (title);
CREATE INDEX idx_advanced_user_query_submitted ON np_users.advanced_user_query (submitted);
CREATE INDEX idx_advanced_user_query_public ON np_users.advanced_user_query (public);