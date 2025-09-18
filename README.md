# Inventorio

A Spring Boot REST API for user authentication and product management.

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL

### Configuration

Set the following properties in `src/main/resources/application.properties`:

```
server.port=8089
admin.username=admin
admin.password=adminpwd123
jwt.secret=your-very-long-secret-key-here
spring.datasource.url=jdbc:postgresql://localhost:5432/inventorio
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
```

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
  -d "username=testuser&password=testpass"
```

#### 1.2. Login with a user
`POST /auth/login`  

Form params: `username`, `password`

Response: JWT token or error message

##### Curl Example
```sh
curl -X POST http://localhost:8089/auth/login \
  -H "Content-Type: application/json" \
  -d "username=testuser&password=testpass"
```

##### 1.2.1. Obtaining JWT Token
After you logged in, you will receive a JWT token in the response, e.g.:

```
{eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTY5NDc2ODAwMCwiZXhwIjoxNjk0ODUzNDAwfQ.4QwQv1K7QvQwQv1K7QvQwQv1K7QvQwQv1K7QvQwQv1K7Q}
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
curl -X GET http://localhost:8089/api/products/1 \
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
  -d '{"code":"ABC123DEFG","name":"Test Product","priceEur":10.00,"isAvailable":true}'
```

#### 2.4. Delete Product (Admin Only)
`DELETE /api/products/{id}`

Headers: `Authorization: Bearer <JWT_TOKEN>`

Requires `ADMIN` role

```sh
curl -X DELETE http://localhost:8089/api/products/1 \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \
  -H "Content-Type: application/json"
```
