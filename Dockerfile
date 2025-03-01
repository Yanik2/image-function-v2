FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists
RUN mvn clean package -DskipTests

COPY target/*.jar function.jar

CMD ["java", "-jar", "function.jar"]