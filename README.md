# Nextprot API

_CAUTION: Nextprot API is at its very early stages of development. Things are likely to change in ways that are not backwards compatible_

This projected is licensed under the terms of the GNU GPL v2.0

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
<ul>
	<li>
		<a href="http://spring.io" target="_blank">	<img alt="Spring" height="30" src="http://blog.goyello.com/wp-content/uploads/2011/12/Logo_Spring_252x150.png"/> </a>
	</li>
	<li>
		<a href="http://structure101.com" target="_blank">	<img alt="Structure101" height="30" src="http://structure101.com/images/s101_170.png"/> </a>
	</li>
</ul>

* [solr](http://lucene.apache.org/solr/) 
* [Virtuoso](http://http://virtuoso.openlinksw.com/)

* Git
* Maven
