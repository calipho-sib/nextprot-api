# Nextprot API

This project is licensed under the terms of the GNU General Public License, version 2 or any later version (see file LICENSE.txt)

## Configure your environment (for eclipse)
```shell
git clone https://github.com/calipho-sib/nextprot-api.git #(1st time) 
cd nextprot-api #(1st time) 
mvn eclipse:eclipse -Dwtpversion=2.0
#Then add the web module into an Apache Tomcat Server (configured in Eclipse) and that's it
#Make sure to add the environment variable -Dspring.profiles.active=DEV on the tomcat startup

#To make it run (by command line)
cd web
mvn jetty:run -Dspring.profiles.active=DEV
```

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
