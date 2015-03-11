ALTER TABLE np_users.user_protein_lists ADD COLUMN public_id varchar(20) NOT NULL;
ALTER TABLE np_users.user_queries ADD COLUMN public_id varchar(20) NOT NULL;

ALTER TABLE np_users.user_protein_lists ADD CONSTRAINT user_protein_lists_pubid_udx UNIQUE (public_id);
ALTER TABLE np_users.user_queries ADD CONSTRAINT user_queries_pubid_udx UNIQUE (public_id);
