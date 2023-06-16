FROM openjdk:17-jdk-alpine

# 필요한 패키지 설치
RUN apk update && apk add --no-cache curl unzip
# 크롬 설치
RUN apk add --no-cache chromium
# 크롬 드라이버 설치
RUN apk add --no-cache chromium-chromedriver
# 크롬 드라이버 바이너리를 PATH에 추가
ENV PATH="/usr/lib/chromium/chromedriver:${PATH}"

ARG JAR_FILE=build/libs/crawling-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]