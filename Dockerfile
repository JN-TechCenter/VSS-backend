# 后端 Dockerfile - 多阶段构建
FROM openjdk:17-jdk-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制构建文件
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# 配置Maven使用华为云镜像
RUN mkdir -p ~/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>' > ~/.m2/settings.xml && \
    echo '<settings>' >> ~/.m2/settings.xml && \
    echo '  <mirrors>' >> ~/.m2/settings.xml && \
    echo '    <mirror>' >> ~/.m2/settings.xml && \
    echo '      <id>huaweicloud</id>' >> ~/.m2/settings.xml && \
    echo '      <name>Huawei Cloud Maven</name>' >> ~/.m2/settings.xml && \
    echo '      <url>https://repo.huaweicloud.com/repository/maven/</url>' >> ~/.m2/settings.xml && \
    echo '      <mirrorOf>central</mirrorOf>' >> ~/.m2/settings.xml && \
    echo '    </mirror>' >> ~/.m2/settings.xml && \
    echo '  </mirrors>' >> ~/.m2/settings.xml && \
    echo '</settings>' >> ~/.m2/settings.xml

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

# 从构建阶段复制构建好的jar文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
