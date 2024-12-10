# Scale 评分系统

编译打包java:
mvn clean package -DskipTests

# 构建镜像
docker build -t scale-app .

# 运行镜像在amd64架构上
docker run -d -p 8080:8080 --platform linux/amd64  my-scale:1.1

# 跟踪日志
docker logs -f -t charming_gould


## 项目简介
Scale 是一个基于 Spring Boot 的评分计算系统，支持动态加载 JavaScript 脚本进行评分计算。
系统采用异步处理方式，支持高并发请求处理。

## 特性
- 动态加载远程JavaScript评分脚本
- 异步处理提升并发性能
- 多环境配置支持
- 完善的日志记录
- UTF-8编码支持
- 线程池资源管理

## 技术栈
- Java 21
- Spring Boot 3.2.0
- Nashorn JavaScript 引擎
- Lombok
- Maven

## 系统架构
- RESTful API设计
- 异步处理支持
- 多环境配置（开发/生产）
- 日志滚动策略
- UTF-8 字符编码支持
- 线程池配置（核心2线程，最大4线程）

## 开发指南
请参考 [API文档](api.md) 和 [部署文档](deployment.md) 了解详细信息。

## API 文档

### 评分计算接口
POST `/scale/compute`

请求体示例： 