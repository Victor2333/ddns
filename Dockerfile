FROM maven:3.8.1-openjdk-16-slim as BUILDER_IMG
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

FROM arm64v8/openjdk:16-slim
COPY --from=BUILDER_IMG /tmp/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]