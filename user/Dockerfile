# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy root pom + ALL module pom.xml files so Maven can resolve the
# multi-module parent POM structure (it checks every declared <module> exists)
COPY pom.xml .
COPY common/pom.xml    common/pom.xml
COPY user/pom.xml      user/pom.xml
COPY demo/pom.xml      demo/pom.xml
COPY generator/pom.xml generator/pom.xml
COPY business/pom.xml  business/pom.xml
COPY gateway/pom.xml   gateway/pom.xml
COPY scheduler/pom.xml scheduler/pom.xml

# Copy only the source code we actually need to compile
COPY common/src common/src
COPY user/src   user/src

# Aliyun Maven Acceleration
RUN mkdir -p /root/.m2 && \
    echo '<settings><mirrors><mirror><id>aliyun</id><mirrorOf>central</mirrorOf><name>Aliyun Maven</name><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

# Build common first, then user (skip tests for speed)
RUN mvn install -pl common -am -DskipTests -q && \
    mvn package -pl user -DskipTests -q

# ---- Run Stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /build/user/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
