#FROM openjdk:8-jdk
#FROM openjdk:8-jdk-slim
FROM openjdk:8-jdk-alpine
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' > /etc/timezone
COPY dbhelp.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

# docker save -o ~/jar/fileName.tar image:version
# docker load < fileName.tar

# docker tag johnnywjh/dbhelp:v.2.3 johnnywjh/dbhelp:v.2.3.m1
# docker save -o ~/fsdownload/dbhelp.arm64.tar johnnywjh/dbhelp:1.02.arm64
# docker load < dbhelp.arm64.tar

######################################
#   openjdk:<version>
#   openjdk:<version>-slim
#   openjdk:<version>-alpine

# 从尺寸上来讲，alpine最小、slim稍大、默认的最大。所以应该尽可能的使用alpine版本的，如果发现程序的运行环境缺少某些东西，那么尝试用slim版本或者默认版本。就目前的经验来讲：

#   1.如果需要操作系统字体库，那么就得使用slim版本或者默认版本。需要操作系统字体库的程序例如：图片验证码、PDF导出。
#   2.如果需要某些Linux标准的动态/静态连接库，那么在alpine版本不行的情况下，尝试slim版本或默认版本。因为alpine版本是一个及其精简的Linux，它删除了很多东西。

#FROM java:8-jre-alpine
#FROM openjdk:8-jre-alpine
#FROM openjdk:8-jdk-alpine
######################################