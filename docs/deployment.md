# Scale 部署文档

## 环境要求
- JDK 21+
- Maven 3.6+
- 内存：至少2GB
- 磁盘：至少1GB可用空间
- Nginx 1.18+

## 构建步骤

1. 构建项目

2. Nginx配置   ```bash
   # 安装nginx（如果未安装）
   sudo apt-get update
   sudo apt-get install nginx

   # 复制nginx配置文件
   sudo cp nginx/scale.conf /etc/nginx/conf.d/

   # 测试配置文件
   sudo nginx -t

   # 重启nginx
   sudo systemctl restart nginx   ```

3. 防火墙配置   ```bash
   # 开放80端口（如果需要）
   sudo firewall-cmd --zone=public --add-port=80/tcp --permanent
   sudo firewall-cmd --reload   ```

## 访问验证
- 通过 Nginx: http://<your-domain>/scale
- 直接访问: http://<your-domain>:8080/scale

## 注意事项
1. 确保 Nginx 和 Java 应用都已正确启动
2. 检查防火墙配置
3. 如果使用云服务器，确保安全组开放了相应端口
4. 建议在生产环境配置 SSL 证书

## Docker Compose 部署

1. 使用 Docker Compose 启动服务   ```bash
   # 构建并启动所有服务
   docker-compose up -d --build

   # 查看服务状态
   docker-compose ps

   # 查看日志
   docker-compose logs -f   ```

2. 停止服务   ```bash
   docker-compose down   ```

## 生产环境部署

1. 使用生产环境配置启动服务
   ```bash
   # 使用生产环境配置文件启动
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build

   # 查看生产环境日志
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f
   ```

2. 服务维护
   ```bash
   # 更新服务（保持数据卷）
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml down
   git pull
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build

   # 清理旧镜像
   docker image prune -f
   ```

## 监控和日志

1. 容器日志位置
   - 应用日志：`./logs/api-prod.log`
   - Docker 日志：`/var/lib/docker/containers/[container-id]/`

2. 健康检查
   ```bash
   # 检查容器状态
   docker-compose ps

   # 检查应用健康状态
   curl http://localhost/scale/actuator/health
   ```

## HTTPS 配置

1. SSL 证书配置
   ```bash
   # 如果使用自签名证书（仅开发环境）
   mkdir -p ssl
   openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
     -keyout ssl/scale.key -out ssl/scale.crt \
     -subj "/C=CN/ST=State/L=City/O=Organization/CN=your-domain.com"
   
   # 如果使用正式证书
   # 将证书文件复制到 ssl 目录
   cp your-certificate.crt ssl/scale.crt
   cp your-private.key ssl/scale.key
   ```

2. 证书权限设置
   ```bash
   chmod 644 ssl/scale.crt
   chmod 600 ssl/scale.key
   ```

3. 重启服务
   ```bash
   docker-compose down
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
   ```

注意：生产环境请使用正式的 SSL 证书，可以从 Let's Encrypt 等机构获取免费证书。

## Docker 镜像构建

1. 构建基础 JDK 镜像
   ```bash
   # 构建 JDK 基础镜像
   docker build -t scale-jdk:21 -f docker/jdk/Dockerfile ./docker/jdk
   ```

2. 构建应用镜像
   ```bash
   # 构建应用镜像
   docker build -t scale-app .
   ```