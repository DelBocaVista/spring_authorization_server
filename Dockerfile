#FROM tomee:8-jre-7.1.0-plus
FROM tomcat:latest
RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./target/auth_server.war /usr/local/tomcat/webapps/ROOT.war
CMD ["catalina.sh","run"]