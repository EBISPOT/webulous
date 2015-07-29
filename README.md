Webulous
===========

Webulous is a server for creating OWL ontologies from spreadsheets. Webulous provides a repository of ontology templates (or design patterns) that can be populated with data
from spreadsheets. Webulous includes an OPPL (Ontology Pre-Processing Language) server for transforming data collected from templates into an OWL ontology. Webulous also has an accompanying
Google Spreadsheet App (https://chrome.google.com/webstore/detail/webulous/noieiladpjihajkdgipcmnjcjcgplood), that connects Google Spreadsheets to a Webulous template server.

There's more information about using Webulous available here http://www.ebi.ac.uk/efo/webulous.

Installation
=============

Webulous can be built with Maven and includes an executable jar with an embedded tomcat, or you can deploy the provided WAR file in your servlet container. Webulous requires
 access to a MongoDB database.

Maven build
-----------

    mvn package

This will build a WAR file in webulous-mvc/target/webulous-boot.war that can be deployed in your
favourite container or executed directly with an embedded tomcat using

Run with embedded tomcat
------------------------

    java -jar    webulous-mvc/target/webulous-boot.war


Or run the Webulous server directly with Docker
------------------------

    docker run -d -p 8080:8080 simonjupp/webpopulous 

Assuming you are running on localhost the webulous server will be running at http://localhost:8080/webulous
