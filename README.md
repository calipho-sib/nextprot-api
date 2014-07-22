# Nextprot API

*** CAUTION: Nextprot API is at its very early stages of development. Things are likely
to change in ways that are not backwards compatible ***

[spring-mvc](http://spring.io) + [solr](http://lucene.apache.org/solr/) + [maven](http://maven.apache.org/)

## Main API Features:
* Provides a Java Spring REST like API at http://localhost:8080/nextprot-api that connects to an RDBMS database (entry based).
* Provides a full-text search mechanism that relies on Solr Indexes (entry, publication and terms based).
* Provides an advanced search engine through a SPARQL endpoint available at http://localhost:8080/sparql (based on Virtuoso datastore)

# Datastores

## Database
*** coming soon ***
## Solr indexes
*** coming soon ***
## RDF datastore 
*** coming soon ***

## Usage, 
  >jetty.sh start

## Testing
Unit testing
Integration testing must start the application

## Questions or comments? 
[Contact us](http://www.nextprot.org/contact/us)