FROM maven:3.8.4-openjdk-8 AS build
ENV HOME=/home/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD pom.xml $HOME
RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "verify", "clean", "--fail-never", "-DskipTests"]
ADD . $HOME
RUN ["mvn", "package", "-DskipTests"]

FROM tomcat:9-jdk16

COPY --from=build /home/usr/app/target/ROOT /usr/local/tomcat/webapps/ROOT

WORKDIR /usr/local/tomcat/bin