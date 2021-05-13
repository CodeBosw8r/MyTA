FROM tomcat:9-jdk16

RUN apt-get update && apt-get install zip telnet --assume-yes

COPY target/myta-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/MyTA.war

RUN mkdir /usr/local/tomcat/webapps/MyTA && unzip /usr/local/tomcat/webapps/MyTA.war -d /usr/local/tomcat/webapps/MyTA 

WORKDIR /usr/local/tomcat/bin