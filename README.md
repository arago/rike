![rike-logo](https://github.com/arago/rike/raw/master/logo.png)

About:
=============

Rike enables teams to autonomously administer their tasks according to the kanban principle and to thus work in a more focused manner. The meta-ticketing/planning tool is based on the kanban principle and embraces elements of other agile methods like extreme programming or scrum. With Rike, team members autonomously choose their tasks according to the so-called "pull principle". Thereby, all team members are equally challenged and able to bring their 
individual strengths to the table. By minimizing simultaneously pending tasks, Rike reduces the throughput time for individual tasks.

  
For further information see:  

* (English) [Press Release](http://www.arago.de/news/arago-makes-project-management-tool-available-as-an-open-source-solution/)  
* (English) [Development Approach](https://github.com/arago/rike/blob/master/DEVELOPMENT_APPROACH.md)
* (German) [rike-projektmanagement-nach-dem-kanban-prinzip](http://www.automatisierungs-experten.de/rike-%E2%80%93-projektmanagement-nach-dem-kanban-prinzip/)
* (German) [die-erste-runde-gamification-bei-arago](http://www.automatisierungs-experten.de/die-erste-runde-gamification-bei-arago/)
* (English) [News Article](http://www.h-online.com/open/news/item/Rike-project-management-tool-open-sourced-1672595.html)
* (German) [News Article](http://www.heise.de/developer/meldung/arago-stellt-Projektmanagement-Tool-Rike-unter-Open-Source-Lizenz-1671177.html)
* (English) [Help](https://github.com/arago/rike/wiki)

Quickstart:
=============

* see [AMI_QUICKSTART](https://github.com/arago/rike/blob/master/AMI_QUICKSTART.md) on how to start a preconfigured EC2 rike instance

or build from source:

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
- [graphviz](http://www.graphviz.org/) (dot)
- java (>= 1.6)
- [liferay](http://www.liferay.com/) (= 6.0.6, with jquery)
- [maven](http://maven.apache.org/) (>= 3) is needed for assembly

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

 