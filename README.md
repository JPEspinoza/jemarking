# JEmarking

Desktop tool to scan pages for Emarking.

Originally made by Villalon: https://github.com/villalon/eMarkingWeb

## Requirements
- Java 8 or higher
- Maven

## Build instructions
- Run `mvn build`
- Run `mvn package`, now the jar file can be found in the target folder

## Run instructions
- On Windows you will need `gsdll64.dll` on the same folder as the jar
- You also need the log4j.properties and moodle.properties files
- Run with `java -jar eMarkingdesktop-0.1.1.jar`
