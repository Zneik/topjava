# Meals rest methods

### Get all user meals
``curl -L -X GET http://localhost:8080/topjava/rest/meals -H "Content-Type: application/json"``

### Get user meal by id
``curl -L -X GET http://localhost:8080/topjava/rest/meals/100008 -H "Content-Type: application/json"``

### Get filtered user meals
``
curl -L -X GET "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&startTime=10:00&endDate=2020-01-31&endTime=14:00" -H "Content-Type: application/json"
``

### Create new user meal
``
curl -L -X POST http://localhost:8080/topjava/rest/meals -H "Content-Type: application/json" --data "{\"dateTime\": \"2020-01-31T23:00:00\",\"description\": \"Легкий перекус\",\"calories\": 2510}"
``

### Update user meal
``
curl -L -X PUT http://localhost:8080/topjava/rest/meals/100008 -H "Content-Type: application/json" --data "{\"id\":\"100008\",\"dateTime\": \"2020-01-31T23:00:00\",\"description\": \"Легкий перекус\",\"calories\": 2510}"
``

### Delete user meal
``
curl -L -X DELETE http://localhost:8080/topjava/rest/meals/100008 -H "Content-Type: application/json"
``