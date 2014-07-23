# Nextprot API

_CAUTION: Nextprot API is at its very early stages of development. Things are likely to change in ways that are not backwards compatible_

# Main API Features:
* Provides a Java Spring REST API at http://localhost:8080/nextprot-api that connects to an RDBMS database (entry based).
* Provides a full-text search mechanism that relies on Solr Indexes (entry, publication and terms based).
* Provides an advanced search engine through a SPARQL endpoint available at http://localhost:8080/sparql (based on Virtuoso datastore)

## Usage, 
```
jetty.sh start
```

You should be able to log into: http://localhost:8080/nextprot-api


## Testing
```
mvn test
```
## Questions or comments? 
[Contact us](http://www.nextprot.org/contact/us)


## Powered by 
* [spring-mvc](http://spring.io) 
* [solr](http://lucene.apache.org/solr/) 
* [Virtuoso](http://http://virtuoso.openlinksw.com/)

* ![Structure101 logo](http://structure101.com/images/s101_170.png "Structure 101") [site](http://structure101.com)


<a href="http://structure101.com">
	<img src="http://structure101.com/images/s101_170.png"/>
</a>

* Git
* Maven