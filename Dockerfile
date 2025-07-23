
# 后端 Dockerfile - 多阶段构建
FROM openjdk:17-jdk-slim AS builder

# 设置工作目录
WORKDIR /app



# 设置 apt 国内清华源
RUN sed -i 's@http://deb.debian.org@https://mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list \
 && sed -i 's@http://security.debian.org@https://mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list

# 安装 maven（用清华源加速）
RUN sed -i 's@http://deb.debian.org@https://mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list \
 && sed -i 's@http://security.debian.org@https://mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list \
 && apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 复制构建文件
COPY pom.xml .


# 配置Maven使用清华源镜像
RUN mkdir -p /root/.m2
COPY settings.xml /root/.m2/settings.xml


# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src src


# 构建应用
RUN mvn clean package -DskipTests


# 运行阶段
FROM openjdk:17-jdk-slim

# 设置 apt 国内清华源
RUN sed -i 's@http://deb.debian.org@https://mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list \
 && sed -i 's@http://security.debian.org@https://mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list

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
