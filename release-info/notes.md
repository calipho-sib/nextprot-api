## 22 sept 2015
All queries run properly on kant (16Gb): OK

3 queries return errors on uat-web2 (2Gb).
#### Errors explained
##### NXQ_00058.rq
Virtuoso 42000 Error The estimated execution time 896 (sec) exceeds the limit of 600 (sec).

Note that on kant the query is run in less than 4 seconds.
##### NXQ_00074.rq
Error The estimated execution time 10878 (sec) exceeds the limit of 600 (sec).

Note that on kant the query is run in less than 490 seconds.
##### NXQ_00096.rq
Error probably due to non availability of remote SPARQL engine (drugbank). Worked properly in later run. 

Was also run properly on kant


