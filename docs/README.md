# Scale 评分系统


采用共享jdk，docker-compose部署java项目

## 创建jdk共享volume
docker volume create jdk-shared

## 构建共享jdk镜像
docker build -t jdk:1.1 ./jdk  

## 运行jdk 在amd64架构上  "jdk-shared:/usr/lib/jvm/java-21-openjdk-amd64" 为jdk共享volume
docker run -d --name jdk-server --platform linux/amd64 -v jdk-shared:/usr/lib/jvm/java-21-openjdk-amd64 jdk:1.1

## 运行java项目
mvn spring-boot:run

## 编译打包java:
mvn clean package -DskipTests

# 构建镜像
docker build -t scale-app .

# 运行镜像在amd64架构上
docker run -d -p 8080:8080 --platform linux/amd64  my-scale:1.1

# 跟踪日志
docker logs -f -t charming_gould

#nginx app
docker-compose up -d
