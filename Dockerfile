# 后端 Dockerfile - 使用预构建的 JAR 文件
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制预构建的 JAR 文件
COPY VSS-backend/target/vision-platform-backend-0.0.1-SNAPSHOT.jar app.jar

# 设置运行时环境
EXPOSE 3002

# 运行应用
CMD ["java", "-jar", "app.jar"]
