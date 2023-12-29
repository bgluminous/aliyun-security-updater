FROM eclipse-temurin:17-jre-alpine

LABEL authors="BGLuminous <i@on.ink>"
LABEL describe="更新阿里云 RDS/ECS/DNS 的可配置定时服务"

COPY ./target/*.jar /home/app.jar

VOLUME /home

RUN bash -c 'touch /home/app.jar'

CMD ["java","-jar","/home/app.jar"]
