#!/bin/bash

set -e

echo "本地docker部署..."

echo "jar 打包 环境: prod"
mvn clean package -DskipTests
docker-compose up -d --build
echo "部署完成！"