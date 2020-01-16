# Java Test

**A FILE PROCESSOR APPLICAION**

**Developer enviroment**
- JDK 11 or higher installed on your system. Set JAVA_HOME enviroment variable.
- Maven 3.6.1 or higher installed.

**Usage**
- Clone or download this repository.
- cd to project folder
- Compile using command "mvn install". This command must generate the file called "application.jar" in the "target" directory.
- Execute unit-test using command "mvn test". This must generate:
	-  the test reports in "target/surfire-reports"
	- code coverage reports in "target/site"

**Tunning**

Support system-properties:
	- *data.processor.count*: The number of thread for concurrent process each data line of the file. Default 1
 	- *data.queue.size*: Increase this value to increase the buffer size of the data that is loaded from the file. Default 100
	- *filtered.value.set.initial.size*: The number of thread for concurrent process each data line of the file. Default 100.
Set this variable in the JAVA_OPTS enviroment variable using "-D{PROPERTY_NAME}={PROPERTY_VALUE} or execute with the command:

*java -jar application.jar input.txt [FILTER-TYPE] [FILTER-VALUE] \\
-Ddata.processor.count=4 -Ddata.queue.size=100 \\
-Dfiltered.value.set.initial.size=100*

Where FILTER-TYPE can be "ID" or "CITY".


**Author**

Bruno Strasser
