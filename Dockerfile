#Stage 1: Build
# Khởi chạy Maven để chạy được JDK 21 đặt tên là build
FROM maven:3.9.8-amazoncorretto-21 AS build

#Copy file pom.xml và thư mục src vào /app để bắt đầu chạy
WORKDIR /app
COPY pom.xml .
COPY src ./src

#Build source code với Maven
RUN mvn package -DskipTests

#Stage 2: create image
# Start with Amazon Correto JDK 21
FROM amazoncorretto:21.0.4

# Set working folder to App and copy complied file from above step
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]