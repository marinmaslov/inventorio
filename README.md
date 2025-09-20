# Inventorio

A Spring Boot REST API for user authentication and product management.

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL

### Limits
- HikariCP connection pool is limited to 2 connections for free-tier PostgreSQL databases (Aiven)

### Configuration

## Way 1
Set the following properties in `src/main/resources/application.properties`:

```
# Server port configuration
server.port=<port_number>

# JWT Secret
jwt.secret=<32B_alphanumerical_string>

# Admin User Credentials
admin.username=<admin_username>
admin.password=<admin_password>

# Logging
logging.level.root=info
logging.level.org.springframework=info
logging.level.com.inventorio=info

# If you wish to use HTTP2
#server.ssl.key-store=classpath:<keystore_file>.p12
#server.ssl.key-store-password=<keystore_password>
#server.ssl.key-store-type=PKCS12
#server.ssl.key-alias=<test_cert>
#server.http2.enabled=true

# Aiven PostgreSQL connection
spring.datasource.url=<db_source_url>
spring.datasource.username=<db_user>
spring.datasource.password=<db_password>

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Limit HikariCP
spring.datasource.hikari.maximum-pool-size=2
spring.datasource.hikari.minimum-idle=1

```
### IMPORTANT
If issues occur while running the app from INTELLIJ, please do the following:
1. Go to `File` > `Project Structure` > `Project`.
2. Ensure `Project SDK` is set to `JDK 17`.
3. Go to `File` > `Settings` > `Build, Execution, Deployment` > `Compiler` > `Java Compiler` and set it to 17.
4. Make sure `Project bytecode version` is set to 17.
5. Rebuild your project.

## Way 2 (Easy Mode)
Download the jar file from the latest [release](https://github.com/marinmaslov/inventorio/releases) and run it with:
```sh
java -jar inventorio-0.0.1-SNAPSHOT.jar --spring.config.location=path/to/application.properties
```

The application.properties file should contain the same properties as shown in Way 1.

### Build & Run

```sh
mvn clean install
mvn spring-boot:run
```

## REST API Endpoints

### 1. Authentication

#### 1.1. Register a new user
`POST /auth/register`  

Form params: `username`, `password` 

Response: Success or error message

##### Curl Example
```sh
curl -X POST http://localhost:8089/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

#### 1.2. Login with a user
`POST /auth/login`  

Form params: `username`, `password`

Response: JWT token or error message

##### Curl Example
```sh
curl -X POST http://localhost:8089/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

##### 1.2.1. Obtaining JWT Token
After you logged in, you will receive a JWT token in the response, e.g.:

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTY5NDc2ODAwMCwiZXhwIjoxNjk0ODUzNDAwfQ.4QwQv1K7QvQwQv1K7QvQwQv1K7QvQwQv1K7QvQwQv1K7Q
```

**Use the token in requests:**
   Add the header:
   ```
   Authorization: Bearer <JWT_TOKEN>
   ```
   Replace `<JWT_TOKEN>` with the token received from the login response.


### 2. Products (Requires JWT)
- Each request includes the `Authorization: Bearer <JWT_TOKEN>` header.
- The DELETE endpoint requires an admin JWT.

#### 2.1. List Products
`GET /api/products`

Headers: `Authorization: Bearer <JWT_TOKEN>`

```sh
curl -X GET http://localhost:8089/api/products \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json"
```

#### 2.2. Get Product by ID
`GET /api/products/{id}`

Headers: `Authorization: Bearer <JWT_TOKEN>`

```sh
curl -X GET http://localhost:8089/api/products/{id} \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json"
```

#### 2.3. Create Product
`POST /api/products`

Headers: `Authorization: Bearer <JWT_TOKEN>`

Body: `JSON` product object

```sh
curl -X POST http://localhost:8089/api/products \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"code":"ABC123DEFG","name":"Test Product","priceEur":10.00,"available":true}'
```

#### 2.4. Delete Product (Admin Only)
`DELETE /api/products/{id}`

Headers: `Authorization: Bearer <JWT_TOKEN>`

Requires `ADMIN` role

```sh
curl -X DELETE http://localhost:8089/api/products/{id} \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \
  -H "Content-Type: application/json"
```
