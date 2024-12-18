#!/bin/bash
#  scp -F /Users/mac/.ssh/config  /Users/mac/Desktop/java/scale-app.tar.gz tencent_cloud:~/app/scale/
#  scp -F  ~/.ssh/config  /Users/mac/Desktop/java/scale-app.tar.gz tencent-cloud:~/app/scale/
# 配置变量
REMOTE_USER="ubuntu"
REMOTE_HOST="42.192.101.59"
REMOTE_DIR="~/app/scale"
ARCHIVE_NAME="scale-app.tar.gz"
SSH_CONFIG="/Users/mac/.ssh/config"
SSH_CONFIG_NAME="tencent-cloud"
# 确保脚本在错误时停止
set -e

echo "开始部署流程..."

# 创建临时部署目录
echo "创建临时部署目录..."
mkdir -p deploy_tmp

# 复制必需文件到临时目录
echo "复制部署文件..."
mkdir -p deploy_tmp/target
cp target/*.jar deploy_tmp/target/app.jar
cp Dockerfile deploy_tmp/
cp docker-compose.yml deploy_tmp/
cp -r nginx deploy_tmp/
cp -r ssl deploy_tmp/
cp items.json deploy_tmp/
mkdir -p deploy_tmp/logs

# 打包文件
echo "打包文件..."
tar -czf $ARCHIVE_NAME -C deploy_tmp .

# 上传文件到服务器
echo "上传文件到服务器..."
# 输出当前用户名
# echo "当前用户名: $USER"
# echo "scp -F $SSH_CONFIG $ARCHIVE_NAME $SSH_CONFIG_NAME:$REMOTE_DIR/"
scp -F $SSH_CONFIG $ARCHIVE_NAME $SSH_CONFIG_NAME:$REMOTE_DIR/

# 在远程服务器上解压文件并启动服务
echo "在远程服务器上部署服务..."
ssh $SSH_CONFIG_NAME "cd $REMOTE_DIR && \
    tar -xzf $ARCHIVE_NAME && \
    rm $ARCHIVE_NAME && \
    docker-compose down || true && \
    docker-compose up -d"




# 检查服务是否启动成功
echo "检查服务是否启动成功..."
ssh  $SSH_CONFIG_NAME "cd $REMOTE_DIR && docker-compose ps"

# 清理本地临时文件
echo "清理本地临时文件..."
rm -rf deploy_tmp
rm $ARCHIVE_NAME

echo "部署完成！"