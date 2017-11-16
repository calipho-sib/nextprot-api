FROM jetty
ADD web/target/nextprot-api-web.war /var/lib/jetty/webapps/root.war
ADD env/preprod-nextprot-application.properties /home/jetty/.config/nextprot-application.properties
EXPOSE 8680
