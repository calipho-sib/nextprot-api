## User module 

Creates User, User applications, User Lists and User Queries


Database schema
### Database creation

Use the script to migrate
```
./flyway.sh DBHOST USER PASSWORD FLYWAY_TARGET
Example:

./flyway.sh crick.isb-sib.ch:5432/np_unit postgres postgres migrate
```


```
mvn flyway:migrate
```


For unit testing the db may be cleaned (drops all tables)
```
mvn flyway:clean
```
