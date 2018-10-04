## Why? [![start with what and why](https://img.shields.io/badge/start%20with-why%3F-brightgreen.svg?style=flat)]()

This is an easy to use tool to visualize OSGi Dependencies in a graph view. 

---------------------------------------------------------------------------------------------------------------

### Requirements

Java 8+


### Contribution [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)]()

Want to contribute? Great! Check out [Contribution Guide](https://github.com/amitjoy/dependency-graph-osgi/blob/master/CONTRIBUTING.md)

-------------------------------------------------------------------------------

#### Project Import

**Import as Maven Project**

Import the project as Existing Maven Project (`File -> Import -> Maven -> Existing Maven Projects`)

-------------------------------------------------------------------------------

#### Building from Source

1. Run `mvn clean package` in `com.amitinside.dependency.graph.osgi`
2. This will build the project
3. The target directory will contain `dependency.graph.osgi-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

-------------------------------------------------------------------------------

### License

This project is licensed under EPL-1.0 [![License](http://img.shields.io/badge/license-EPL-blue.svg)](http://www.eclipse.org/legal/epl-v10.html)

---------------------------------------------------------------------------------

### Usage

1. You need to create an OBR Index file using bnd - `java -jar biz.aQute.bnd.jar index */target/*.jar`
2. You can download bnd from this URL - https://goo.gl/D6RN6K
3. The aforementioned command will create an OBR Index XML file in your project workspace directory
4. Now create a `bundles.txt` file listing the bundles whose dependency graph will be prepared
5. The `bundles.txt` file must comprise the bundle symbolic names in separate lines
6. `java -jar dependency.graph.osgi-0.0.1-SNAPSHOT-jar-with-dependencies.jar -o index.xml -b bundles.txt -e`
7. `-e` is used to show Dependency Graph edge labels

--------------------------------------------------------------------------------------------------------

### Tools Used

1. https://github.com/bndtools/bnd
2. http://graphstream-project.org/

--------------------------------------------------------------------------------------------------------

<img width="1318" alt="bildschirmfoto 2018-10-04 um 20 39 13" src="https://user-images.githubusercontent.com/13380182/46495393-95ece980-c815-11e8-979a-38b62188056e.png">

-------------------------------------------------------------------------------

