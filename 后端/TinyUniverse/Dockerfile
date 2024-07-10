
FROM maven:3.6.0-jdk-8-slim as build

# Copy local code to the container image.
WORKDIR /TinyUniverse
COPY . /TinyUniverse

COPY pom.xml /app/

#COPY xxl-job-admin .

# Build a release artifact.
RUN mvn package -DskipTests


# 选择运行时基础镜像
FROM alpine:3.13

# 安装依赖包，如需其他依赖包，请到alpine依赖包管理(https://pkgs.alpinelinux.org/packages?name=php8*imagick*&branch=v3.13)查找。
# 选用国内镜像源以提高下载速度
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tencent.com/g' /etc/apk/repositories \
    && apk add --update --no-cache openjdk8-jre-base \
    && rm -f /var/cache/apk/*

# 使用 HTTPS 协议访问容器云调用证书安装 \
RUN apk add ca-certificates
FROM moxm/java:1.8-full as builder

# 容器默认时区为UTC，如需使用上海时间请启用以下时区设置命令
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone

# 指定运行时的工作目录
WORKDIR /TinyUniverse

# 将构建产物jar包拷贝到运行时目录中
COPY --from=build /TinyUniverse/ailu-server/target/*.jar .

EXPOSE 8082

# Run the web service on container startup.
CMD ["java","-jar","/TinyUniverse/ailu-server-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]




