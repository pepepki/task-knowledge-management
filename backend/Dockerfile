# 1. ビルドステージ
FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /app

# Gradle Wrapper と設定ファイルを先にコピー
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# 実行権限を付与して依存関係をダウンロード
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon dependencies

# ソースコードをコピーしてビルド
COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

# 2. 実行ステージ
FROM eclipse-temurin:25-jre-noble
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]