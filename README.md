Webulous
===========

Webulous is a server for creating OWL ontologies from spreadsheets. Webulous provides a repository of ontology templates (or design patterns) that can be populated with data
from spreadsheets. Webulous includes an OPPL (Ontology Pre-Processing Language) server for transforming data collected from templates into an OWL ontology. Webulous also has an accompanying
Google Spreadsheet App (available from Google app store soon), that connects Google Spreadsheets to a Webulous template server.

Installation
=============

Webulous can be built with Maven and includes an executable jar with an embedded tomcat, or you can deploy the provided WAR file in your servlet container. Webulous requires
 access to a MongoDB database.

Maven build
-----------

    mvn package

Run with embedded tomcat
------------------------

    java -jar webulous-server/webulous-tomcat/target/webulous-boot.jar

Find the WAR file
------------------------

    webulous-server/webulous-war/target/webulous-<version>.war

