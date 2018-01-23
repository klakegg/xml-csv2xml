FROM maven:3.3.9-jdk-8 AS mvn

ADD . $MAVEN_HOME

RUN cd $MAVEN_HOME \
 && mvn -B clean package \
 && mv $(ls $MAVEN_HOME/target/*.jar | grep jar-with-dependencies | head -1) /csv2xml.jar



FROM java:8-jre-alpine

COPY --from=mvn /csv2xml.jar /csv2xml.jar

VOLUME /src /target

WORKDIR /src
ENTRYPOINT ["java", "-jar", "/csv2xml.jar"]
