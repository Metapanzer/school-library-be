## How to run the project

### 1. Install dependencies
```bash
mvn clean install -DskipTests
```

### 2. Run docker compose to setup postgres database
```bashbash
docker compose up -d
```

### 3. Change application.properties file to run locally
Change `src/main/resources/application.properties`:
```
spring.profiles.active=dev
```

### 4. Start dev server
```bash
mvn spring-boot:run
```

---