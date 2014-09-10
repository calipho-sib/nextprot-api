/**********************************/
/* Table Name: user_applications */
/**********************************/
CREATE TABLE np_users.user_applications(
  application_id BIGINT NOT NULL,
  application_name VARCHAR(100) NOT NULL,
  application_description VARCHAR(100) NOT NULL,
  application_organisation VARCHAR(100) NOT NULL,
  application_responsible_name VARCHAR(100) NOT NULL,
  application_responsible_email VARCHAR(100) NOT NULL,
  application_website VARCHAR(100),
  application_owner VARCHAR(100),
  application_token VARCHAR(512),
  application_state VARCHAR(512)
);

ALTER TABLE np_users.user_applications ADD CONSTRAINT PK_user_applications PRIMARY KEY (application_id);

--ALTER TABLE np_users.user_applications ADD CONSTRAINT FK_applications_owner FOREIGN KEY (owner) REFERENCES np_users.user_applications (application_id);
-- Check backup histroy http://www.youlikeprogramming.com/2013/05/automatic-backup-of-any-postgresql-table-using-one-trigger-function/

