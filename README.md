# Nextprot API

*** CAUTION: Nextprot API is at its very early stages of development. Things are likely
to change in ways that are not backwards compatible ***

[spring-mvc](http://spring.io) + [solr](http://lucene.apache.org/solr/) + [maven](http://maven.apache.org/)

## Main API Features:
* Provides a Java Spring REST like API at http://localhost:8080/nextprot-api that connects to to RDBMS database (entry based)
* Provides a full-text search mechanism that relies on Solr Indexes
* Provides an advanced search engine through a SPARQL endpoint available at http://localhost:8080/sparql 


## Solr installation and usage
  >mkdir solr-4.5.0
  >rsync -avz --delete npteam@crick:/work/devtools/solr-4.5.0/ solr-4.5.0/

## start/kill solr
  >java -Dnextprot.solr -Xmx1024m -jar -Djetty.port=8985 start.jar &
  >pkill -f nextprot.solr

## Usage, 
  >jetty.sh start

## Testing
Unit testing
Integration testing must start the application

## Questions or comments? 
[contact-us](http://www.nextprot.org/contact/us)