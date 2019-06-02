FROM openjdk:8-jdk-alpine
RUN apk add --no-cache git
RUN apk add --no-cache openssh
RUN apk add --no-cache maven

RUN git clone https://github.com/teodossidossev/ipb-platform.git /app
WORKDIR /app/ipb-platform

RUN mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=18.3 -Dpackaging=jar -Dfile=../ojdbc8.jar
RUN mvn -Dmaven.test.skip=true package

EXPOSE 8080:8080

CMD ["java", "-jar", "target/ipb-platform-0.0.1-SNAPSHOT.jar"]
