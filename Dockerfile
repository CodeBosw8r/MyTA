FROM tomcat:9-jdk16

RUN apt-get update && apt-get install zip curl --assume-yes && \
  mkdir /usr/local/tomcat/webapps/MyTA && \
  curl -L https://github.com/CodeBosw8r/MyTA/releases/download/v1_1_0/MyTA.war -o /usr/local/tomcat/webapps/MyTA.war && \
  unzip /usr/local/tomcat/webapps/MyTA.war -d /usr/local/tomcat/webapps/MyTA 

WORKDIR /usr/local/tomcat/bin