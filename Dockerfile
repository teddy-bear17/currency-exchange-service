FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/currency-exchange-service.war /usr/local/tomcat/webapps/currency-exchange-service.war

EXPOSE 8080

CMD ["catalina.sh", "run"]