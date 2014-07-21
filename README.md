# Nextprot API

***CAUTION: Nextprot API is in the very early stages of development. Things are likely
to change in ways that are not backwards compatible***

[spring-mvc](http://spring.io) + [solr](http://lucene.apache.org/solr/) + [maven](http://maven.apache.org/)

Main Client Features:
* generate XML export 
* Search API
* Basket API

## Backends
nextprot-search from git (npteam@miniwatt:/work/repos/nextprot-api.git)
[solr](http://lucene.apache.org/solr/)


## Solr installation and usage
  >mkdir solr-4.5.0
  >rsync -avz --delete npteam@crick:/work/devtools/solr-4.5.0/ solr-4.5.0/

## start/kill solr
  >java -Dnextprot.solr -Xmx1024m -jar -Djetty.port=8985 start.jar &
  >pkill -f nextprot.solr

## Installation
  >git clone npteam@miniwatt:/work/repos/nextprot-search.git
  >git clone npteam@miniwatt:/work/repos/nextprot-api.git

## Usage, 
update and install latest sources
  >cd nextprot-api && git pull origin 
  >mvn install

## note that test spring profile links to a crick db with a functional np_users schema
run the application
  >cd nextprot-api && git pull origin

# >nohup mvn jetty:run -Djetty.port=8282 -Dnextprot.api -Djetty.jmxrmiport=12345
# >nohup mvn jetty:run -Djetty.port=8282 -Dnextprot.api -Djetty.jmxrmiport=12345 -Dspring.profiles.active=test
  >nohup mvn jetty:run -Dspring.profiles.active=dev&

## Testing
Unit testing
Integration testing must start the application

