# Spring Boot Demo Project

A Spring Boot project with MyBatis Plus integration that provides a REST API endpoint for processing item data.

## Requirements

- Java 8 or higher
- Maven
- MySQL
- Nginx (for reverse proxy)

## Setup

1. Create a MySQL database named `demo`
2. Update the database configuration in `src/main/resources/application.yml` if needed
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoint

POST `/api/v1/`

Request body example:
```json
{
  "items": [
    {
      "name": "John Doe",
      "age": 30,
      "sex": "male",
      "birthday": "1993-01-01",
      "address": "123 Main St",
      "phone": "1234567890",
      "email": "john@example.com",
      "remark": "Some notes"
    }
  ]
}
``` 