/**********************************/
/* Table Name: user_applications */
/**********************************/
CREATE TABLE np_users.user_applications(
  application_id BIGSERIAL NOT NULL,
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

ALTER TABLE np_users.user_applications ADD CONSTRAINT PK_user_applications PRIMARY KEY (application_id);

--ALTER TABLE np_users.user_applications ADD CONSTRAINT FK_applications_owner FOREIGN KEY (owner) REFERENCES np_users.user_applications (application_id);
-- Check backup histroy http://www.youlikeprogramming.com/2013/05/automatic-backup-of-any-postgresql-table-using-one-trigger-function/

