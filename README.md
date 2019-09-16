<img width="303" alt="bildschirmfoto 2018-10-06 um 17 52 35" src="https://user-images.githubusercontent.com/13380182/46573106-a2498180-c990-11e8-8fc0-f1a09c8764c5.png">

## Why? [![start with what and why](https://img.shields.io/badge/start%20with-why%3F-brightgreen.svg?style=flat)]()

This is an easy to use tool to visualize OSGi Dependencies in a Graph. In addition, this tool also supports detection of cyclic dependencies in the plotted graph. The primary goal is to minimize the effort in analyzing big software projects based on OSGi. 

-------------------------------------------------------------------------------------------------------------------

### Requirements

Java 8+


### Contribution [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)]()
-
Want to contribute? Great! Check out [Contribution Guide](https://github.com/amitjoy/dependency-graph-osgi/blob/master/CONTRIBUTING.md)

----------------------------------------------------------------------------------------------------------

#### Project Import

**Import as Maven Project**

Import the project as an `Existing Maven Project` (`File -> Import -> Maven -> Existing Maven Projects`)

-----------------------------------------------------------------------------------------------------------

#### Building from Source

1. Run `mvn clean package` in `com.amitinside.dependency.graph.osgi`
2. This will build the project
3. The target directory will contain `dependency.graph.osgi-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

----------------------------------------------------------------------------------------------------------

### License

This project is licensed under EPL-2.0 [![License](http://img.shields.io/badge/license-EPL-blue.svg)](http://www.eclipse.org/legal/epl-2.0)

--------------------------------------------------------------------------------------------------------

### Usage

To use this application, you need an OBR (`OSGi Bundle Repository`) Index XML File.

#### OBR Index Generation

1. You need to download bnd from this [link](https://goo.gl/xoYH7J)
2. Using terminal, switch to your workspace directory and execute - `java -jar biz.aQute.bnd.jar index */target/*.jar`
3. This assumes that all the target artifacts are kept in `target` directory in respective projects
4. Alternatively, you can copy all your project JARs inside a separate directory
5. Switch to this newly created directory in command line and execute `java -jar biz.aQute.bnd.jar index *.jar`
6. Both the aforementioned commands will generate OBR `index.xml` in the respective directories where the command is executed

#### Create Bundles List

1. You need to have a file comprising the bundle symbolic names of the bundles whose dependencies will be plotted on the graph
2. You can create a `bundles.txt` (or give it a name of your choice) with bundle symbolic names in separate lines. You can also use wildcards. For example, `com.google.*` will consider all the bundles whose symbolic names start with `com.google.` Apart from it, you can also use negations. For example, `!org.apache*` would remove all the bundles from the Graph whose symbolic names start with `org.apache`.

####

Help Command: 

```
usage: Dependency Graph in OSGi - Help
 -?                 Show Help
 -bundles <arg>     Bundle List File Location
 -cycle             Check for Cycle Existence
 -debug             Turn on Debug Mode
 -edge              Show Edge Labels
 -help              Show Help
 -ns <arg>          Namespace Type to Plot [ALL, PACKAGE, SERVICE,
                    IDENTITY, EE, NATIVE, CONTENT, IMPLEMENTATION,
                    CONTRACT, BUNDLE, HOST, CUSTOM] (Default ALL)
 -ns_custom <arg>   Custom Namespace (Needs to be set if ns option is set
                    to CUSTOM
 -obr <arg>         OBR Index File Location
```

#### Example

`java -jar dependency.graph.osgi.jar -obr index.xml -bundles bundles.txt -edge` - Plot the matched bundles from bundles.txt using the specified OBR index.xml with edge labels

----------------------------------------------------------------------------------------------------------

### Tools Used

1. https://bnd.bndtools.org
2. http://graphstream-project.org

------------------------------------------------------------------------------------------------------------

<img width="1422" alt="bildschirmfoto 2018-10-06 um 16 19 07" src="https://user-images.githubusercontent.com/13380182/46572293-a8853100-c983-11e8-8537-4b0a77426c19.png">

----------------------------------------------------------------------------------------------------------

