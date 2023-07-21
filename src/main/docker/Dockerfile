FROM openjdk:17-jdk-slim
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone
COPY dbhelp.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]