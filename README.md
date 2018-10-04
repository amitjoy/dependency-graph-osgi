## Why? [![start with what and why](https://img.shields.io/badge/start%20with-why%3F-brightgreen.svg?style=flat)]()

This is an easy to use tool to visualize OSGi Dependencies in a graph view. 

---------------------------------------------------------------------------------------------------------------

### Requirements

1. Java 8+


### Contribution [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)]()

Want to contribute? Great! Check out [Contribution Guide](https://github.com/amitjoy/dependency-graph-osgi/blob/master/CONTRIBUTING.md)

-------------------------------------------------------------------------------

#### Project Import

**Import as Maven Project**

Import the project as Existing Maven Projects (`File -> Import -> Maven -> Existing Maven Projects`)

-------------------------------------------------------------------------------

#### Building from Source

Run `mvn clean install` in the project root directory

-------------------------------------------------------------------------------

### License

This project is licensed under EPL-1.0 [![License](http://img.shields.io/badge/license-EPL-blue.svg)](http://www.eclipse.org/legal/epl-v10.html)

---------------------------------------------------------------------------------

### Usage

1. You need to create an OBR Index file using bnd - `bnd index */target/*.jar`
2. This will create an OBR Index XML file in your project workspace directory
3. Now create a `bundles.txt` file listing the bundles whose dependency graph will be prepared
4. `java -jar dependency.graph.osgi-0.0.1-SNAPSHOT-jar-with-dependencies.jar -o index.xml -b bundles.txt`

--------------------------------------------------------------------------------------------------------

### Tools Used

1. https://github.com/bndtools/bnd
2. http://graphstream-project.org/

--------------------------------------------------------------------------------------------------------
