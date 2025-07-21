# 后端 Dockerfile - 多阶段构建
FROM openjdk:17-jdk-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制构建文件
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# 下载依赖
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src src

# 构建应用
RUN ./mvnw clean package -DskipTests

# 运行阶段
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制构建好的 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 设置运行时环境
EXPOSE 3002

# 运行应用
CMD ["java", "-jar", "app.jar"]
