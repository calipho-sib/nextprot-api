ALTER TABLE np_users.user_protein_lists DROP CONSTRAINT list_u
ALTER TABLE np_users.user_protein_lists ADD CONSTRAINT list_u UNIQUE (owner_id, list_name)
DROP INDEX np_users.user_protein_lists_name_udx