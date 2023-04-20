FROM openjdk:17
ENV TZ="Asia/Tbilisi"
COPY target/*.jar aws.jar
ENTRYPOINT ["java","-jar","/aws.jar"]
