About:
=============

Rike is a meta-ticketing/planning system based on kanban.  
It focuses on self-organization and peer-review.  

![rike-logo](raw/tip/logo.png)
  
For further information see:  
  
* (German) [rike-projektmanagement-nach-dem-kanban-prinzip](http://www.automatisierungs-experten.de/rike-%E2%80%93-projektmanagement-nach-dem-kanban-prinzip/)
* (German) [die-erste-runde-gamification-bei-arago](http://www.automatisierungs-experten.de/die-erste-runde-gamification-bei-arago/)

Quickstart:
=============

* install all maven dependencies and system requirements
* package the arago-base-theme and arago-base-layout
* package the arago-rike webapp
* import the schema into the database
* configure the system property `-Dde.arago.data.util.datasource.rike`  
( e.g. `-Dde.arago.data.util.datasource.rike=jdbc:mysql://127.0.0.1/rike?user=rike&amp;password=123&amp;useUnicode=true&amp;characterEncoding=UTF-8` )
* deploy all .war to the portlet server
* create a new page, select an arago layout and the arago skin
* add rike portlets

System Requirements:
=============

- mysql (>= 5)
- graphviz (dot)
- java (>= 1.6)
- liferay (= 6.0.6, with jquery)
- maven (>= 3) is needed for assembly

License:
=============

MIT - see LICENSE or [http://opensource.org/licenses/MIT](http://opensource.org/licenses/MIT)  
Copyright (c) 2010 arago AG, [http://www.arago.de/](http://www.arago.de/)

Bugs and Suggestions
=============

* please use the issue options of the sourcecode hoster
* contact: rike-dev (at) arago.de

Known Issues
=============

* disable portlet validation: in `portal-ext.properties` add `portlet.xml.validate=false`
* disable jsp strict quote escaping: `-Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false`
* jquery needs to be provided by liferay (via `javascript.barebone.files` or `javascript.everything.files` in `portal-ext.properties`)

 