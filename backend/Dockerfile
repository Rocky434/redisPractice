FROM eclipse-temurin:21-jdk

# 設定工作目錄
WORKDIR /app

# 複製 Maven Wrapper 腳本與設定
COPY mvnw mvnw
COPY .mvn/ .mvn/

# 複製 pom.xml 先讓 Docker 下載依賴，利用快取
COPY pom.xml .

# 先下載所有 Maven 依賴（離線模式，避免每次build都下載）
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# 複製原始碼
COPY . .

# 啟動 Spring Boot
CMD ["./mvnw", "spring-boot:run"]
