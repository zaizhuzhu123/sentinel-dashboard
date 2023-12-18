
# Dockerfile
# 基于的镜像
FROM ibm-semeru-runtimes:open-8u362-b09-jdk

VOLUME /tmp
ADD sentinel-dashboard.jar app.jar

# -Djava.security.egd=file:/dev/./urandom 可解决tomcat可能启动慢的问题
# 具体可查看：https://www.cnblogs.com/mightyvincent/p/7685310.html
ENV DB_ADDRESS 127.0.0.1:3306
ENV DB_NAME sentinel-dashboard
ENV DB_USER root
ENV DB_PASSWORD root
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.datasource.address=${DB_ADDRESS}","--spring.datasource.name=${DB_NAME}","--spring.datasource.username=${DB_USER}","--spring.datasource.password=${DB_PASSWORD}"]

# 对外端口a
EXPOSE 8080