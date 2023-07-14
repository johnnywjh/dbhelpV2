#FROM openjdk:17-jdk-slim
#RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone
#COPY dbhelp.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]

#FROM openjdk:8u275
#VOLUME /tmp
#ADD dbhelp.jar app.jar
#RUN sh -c 'touch /app.jar'
#ENV TZ="Asia/Shanghai"
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#ENV JVM_OPTS="-XX:MaxRAMPercentage=80.0"
#ENV JAVA_OPTS=""
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar $APP_OPTS" ]


FROM openjdk:17-jdk-slim as builder
WORKDIR application
COPY dbhelp.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:17-jdk-slim
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone
ENV JVM_OPTS="-XX:MaxRAMPercentage=80.0"
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JVM_OPTS $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]

# docker build -t johnnywjh/dbhelp:v.2.27 .