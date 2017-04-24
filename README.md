# IBWUpdater Server [![Build Status](https://travis-ci.org/ThULB/IBWUpdater-Server.svg?branch=master)](https://travis-ci.org/ThULB/IBWUpdater-Server) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/e276ce11390e4000bb9711c9e44864ba)](https://www.codacy.com/app/ThULB/IBWUpdater-Server?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=adlerre/IBWUpdater-Server&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/e276ce11390e4000bb9711c9e44864ba)](https://www.codacy.com/app/ThULB/IBWUpdater-Server?utm_source=github.com&utm_medium=referral&utm_content=adlerre/IBWUpdater-Server&utm_campaign=Badge_Coverage) [![Dependency Status](https://www.versioneye.com/user/projects/583339f8eaa74b004633a88f/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/583339f8eaa74b004633a88f)
WinIBW Updater Server

## Getting Started

This Project requires at least Java 8, Maven 3 and Firefox/Chrome installed to build.

Build Project with this command:

    $ mvn clean install
    
or download latest [release](https://github.com/adlerre/IBWUpdater-Server/releases).
    
Before first start of the Server we need a **persistence.xml**, like this. (By default the H2 library is included.)
    
    <?xml version="1.0" encoding="UTF-8" ?>
    <persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
      version="2.0" xmlns="http://java.sun.com/xml/ns/persistence">
      <persistence-unit name="IBWUpdater" transaction-type="RESOURCE_LOCAL">
        <mapping-file>META-INF/ibwupdater-mappings.xml</mapping-file>
        <properties>
          <property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
          <property name="javax.persistence.jdbc.url" value="jdbc:h2:file:{DataDir}ibwupdater"/>
          <property name="javax.persistence.jdbc.user" value="sa" />
          <property name="javax.persistence.jdbc.password" value="" />
          <property name="eclipselink.logging.level" value="WARNING" />
          <property name="eclipselink.ddl-generation" value="create-or-extend-tables" />
        </properties>
      </persistence-unit>
    </persistence>
    
You needed to create this file on **resources/META-INF/** within configuration directory.

    # on UNIX (default Application config dir)
    $ mkdir -p ~/.ibwupdater/resources/META-INF
    # on Windows
    $ md %LOCALAPPDATA%\IBWUpdater\resources\META-INF
    # or use your own configuration dir with parameter
    $ ... updater.jar --configDir {configDir}

If you want to use a other Database than H2, you needed to create the **lib/** directory within configuration directory and put the needed library to this.

    # on UNIX (default Application config dir)
    $ mkdir -p ~/.ibwupdater/lib
    # on Windows
    $ md %LOCALAPPDATA%\IBWUpdater\lib

Afterward set the **javax.persistence.jdbc.url** to your DB.

**Running Server**

Run Server with:

    $ java -jar updater.jar


Now you should be able to access the WEB-Interface on [http://localhost:8085/](http://localhost:8085/).
 
**Access Server**

REST-API:

    http://localhost:8085/application.wadl


WEB-Interface:

    http://localhost:8085/
    
## Commandline Options

For a help and/or overview about default settings run:

    $ java -jar updater.jar -h
    
* **-h, --help**

  Print help message

* **--host**

  Set host to listen on

* **-p, --port**<br />
  *Default:* `8085`
  
  Set port to listen on
  
* **-cd, --configDir**

  Set configuration dir
