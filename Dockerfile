
# 后端 Dockerfile - 简化构建
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 安装curl和maven
RUN apt-get update && apt-get install -y curl maven && rm -rf /var/lib/apt/lists/*

# 复制项目文件
COPY . .

# 构建项目
RUN mvn clean package -DskipTests

# 复制构建好的jar文件
RUN cp target/*.jar app.jar

# 暴露端口
EXPOSE 3002

# 启动应用
CMD ["java", "-jar", "app.jar"]
