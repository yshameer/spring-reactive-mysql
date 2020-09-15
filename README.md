#Spring Reactive MySQL 

App provides basic CRUD operations connecting to MySql database using reactive relational database driver (R2DBC)

This app is tested connecting to Aurora MySQL

## Pre-Req: Update below properties in application.properties file to connect to your MySQL db of choice
``` properties
spring.r2dbc.url=r2dbc:pool:mysql://<<Your MySQL Host>>:3306/customer
spring.r2dbc.username=<<User Name>>
spring.r2dbc.password=<<Password>>
```

Create table in your mysql schema
```sql
CREATE TABLE customer ( id SERIAL PRIMARY KEY, customer_name VARCHAR(100) NOT NULL, customer_type VARCHAR(100) NOT NULL, customer_status VARCHAR(100) NOT NULL);
```

Start app: 
```bash 
./mvnw spring-boot:run
```

Sample requests to test APIs

Create a record:
```http request
curl --header "Content-Type: application/json" \
    --request POST \
    --data '{"customer_name":"Bob","customer_type":"Publisher","customer_status": "Active"}' \
    http://127.0.0.1:8080
```

Get all customers:
```http request
curl http://127.0.0.1:8080
```

Get a record:
```http request
curl http://127.0.0.1:8080/customer?id=2
```

Update a record:
```http request
curl --header "Content-Type: application/json" \
    --request PUT \
    --data '{"id":"3","customer_name":"Bob","customer_type":"Publisher","customer_status": "InActive"}' \
    http://127.0.0.1:8080
```

Delete a record:
```http request
curl --header "Content-Type: application/json" \
    --request DELETE \
    http://127.0.0.1:8080?id=2
```

## Test profile uses in memory (H2) database