# 使用 JDK 21 作为基础镜像
# FROM eclipse-temurin:21-jdk-jammy
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:21
# FROM subing-local-jdk:21
# 设置工作目录
WORKDIR /app

# 复制 jar 包到容器中
COPY target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 暴露端口
EXPOSE 8080

# 设置时区为上海
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


CMD ["java", "-jar", "/app/app.jar"]