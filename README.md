# ipb-platform (MSE 2018 @ Uni-Ruse)

# How to get the app running

NOTE: You need to have Oracle 18.3c installed.

1. Download "ojdbc8.jar" from here: 

https://www.oracle.com/technetwork/database/application-development/jdbc/downloads/jdbc-ucp-183-5013470.html

2. Navigate to the folder where you downloaded the JAR (through cmd) and install via Maven with the following command:

mvn install:install-file -Dfile=ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=18.3 -Dpackaging=jar

3. Replace the Oracle username/password in the project's "application.properties" with your own

4. Run via Eclipse / Spring Tool Suite