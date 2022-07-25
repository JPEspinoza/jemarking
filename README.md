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
- You will need `gsdll64.dll`, `log4j.properties` and `moodle.properties` on the same folder as the jar
- Run with `java -jar eMarkingdesktop-0.1.1.jar`

## TODO
- Alert the user if tests from different sections are in the pdf
- Improve QR recognition