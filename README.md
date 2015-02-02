# Nextprot API

This project is licensed under the terms of the GNU General Public License, version 2 or any later version (see file LICENSE.txt)

## Configure your environment (for eclipse)
```
mvn eclipse:eclipse -Dwtpversion=2.0
#Then add the web module into an Apache Tomcat Server (configured in Eclipse) and that's it
#Make sure to add the environment variable -Dspring.profiles.active=DEV on the tomcat startup

#To make it run (by command line)
cd web
mvn jetty:run -Dspring.profiles.active=DEV
```

You should be able to log into: [http://localhost:8080/nextprot-api-web]

# Main API Features:
* Provides a Java Spring REST API at http://localhost:8080/nextprot-api that connects to an RDBMS database (entry based).
* Provides a full-text search mechanism that relies on Solr Indexes (entry, publication and terms based).
* Provides an advanced search engine through a SPARQL endpoint available at http://localhost:8080/sparql (based on Virtuoso datastore)


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
	<li>
		<a href="http://www.ej-technologies.com/products/jprofiler/overview.html" target="_blank">
		             <img alt="Java Profiler" height="30" src="https://d325ruyeyianrs.cloudfront.net//mgJIoWeBGjESI3wvaduBhyUarReoVzS8Qq2ppfxyUVv.png"/> 
		</a>
	</li>
</ul>

* [solr](http://lucene.apache.org/solr/) 
* [Virtuoso](http://http://virtuoso.openlinksw.com/)

* Git
* Maven
