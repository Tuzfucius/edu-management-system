# ============================
# Stage 1: Maven 构建
# ============================
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 先复制 pom.xml，利用 Docker 层缓存加速依赖下载
COPY pom.xml ./
RUN mvn dependency:go-offline -B || true

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================
# Stage 2: JRE 运行
# ============================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
