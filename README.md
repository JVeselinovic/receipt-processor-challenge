
# Technology used
Java 17,  Spring Boot '3.1.1', JUnit 5 and Gradle 
that uses dependencies defined in build.gradle for spring boot starter, starter web, spring test, spring validation, 
jakarta validation, lombok and docker starter.

lombok annotation is used for model classes. 
Validation annotations are used based on the OpenAPI specification defined in api.yml requirements.
# How to run the app using docker
Get the project receipt-processor-challenge. 
The solution jar is at jar/ folder
Build the docker with spring boot and java and project jar:
* ```docker build --build-arg JAR_FILE= jar/receipt-processor-challenge-0.0.1-SNAPSHOT.jar -t proj:myapp .```

"." (period) at the end.
If you use "./gradlew clean build" to build again the newest jar will be at build/libs

![build with docker.png](examples%2Fimages%2Fbuild%20with%20docker.png)

to run the app:
* ```docker run -p 8080:8080 proj:myapp```

![run app with docker.png](examples%2Fimages%2Frun%20app%20with%20docker.png)


Spring boot application will start on port you specified above, in this case http://localhost:8080 
You can test the Get and Post methods  using Postman (or Insomnia) or just with curl command

200 response - post receipt and get id:
![postman post and get id.png](examples%2Fimages%2Fpostman%20post%20and%20get%20id.png)

400 response for request validation, if some filed is missing:
![postman bad request.png](examples%2Fimages%2Fpostman%20bad%20request.png)

200 response - Get points for receipt
![get points.png](examples%2Fimages%2Fget%20points.png)

404 response - Get points when receipt id is not found
![receipt id not found.png](examples%2Fimages%2Freceipt%20id%20not%20found.png)

# How to run the app using Spring Boot
* ./gradlew test - run tests
* ./gradlew clean build
* ./gradlew bootRun
---

## Summary of API Specification

### Endpoint: Process Receipts

* Path: `/receipts/process`
* Method: `POST`
* Payload: Receipt JSON
* Response: JSON containing an id for the receipt.

Description:

Takes in a JSON receipt (see example in the example directory) and returns a JSON object with an ID generated by your
code.

The ID returned is the ID that should be passed into `/receipts/{id}/points` to get the number of points the receipt
was awarded.

How many points should be earned are defined by the rules below.

Reminder: Data does not need to survive an application restart. This is to allow you to use in-memory solutions to track
any data generated by this endpoint.

Example Response:

```json
{ "id": "7fb1377b-b223-49d9-a31a-5a02701dd310" }
```

## Endpoint: Get Points

* Path: `/receipts/{id}/points`
* Method: `GET`
* Response: A JSON object containing the number of points awarded.

A simple Getter endpoint that looks up the receipt by the ID and returns an object specifying the points awarded.

Example Response:

```json
{ "points": 32 }
```

---

# Rules

These rules collectively define how many points should be awarded to a receipt.

* One point for every alphanumeric character in the retailer name.
* 50 points if the total is a round dollar amount with no cents.
* 25 points if the total is a multiple of `0.25`.
* 5 points for every two items on the receipt.
* If the trimmed length of the item description is a multiple of 3, multiply the price by `0.2` and round up to the
  nearest integer. The result is the number of points earned.
* 6 points if the day in the purchase date is odd.
* 10 points if the time of purchase is after 2:00pm and before 4:00pm.

## Examples

```json
{
  "retailer": "Target",
  "purchaseDate": "2022-01-01",
  "purchaseTime": "13:01",
  "items": [
    {
      "shortDescription": "Mountain Dew 12PK",
      "price": "6.49"
    },{
      "shortDescription": "Emils Cheese Pizza",
      "price": "12.25"
    },{
      "shortDescription": "Knorr Creamy Chicken",
      "price": "1.26"
    },{
      "shortDescription": "Doritos Nacho Cheese",
      "price": "3.35"
    },{
      "shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
      "price": "12.00"
    }
  ],
  "total": "35.35"
}
```

```text
Total Points: 28
Breakdown:
     6 points - retailer name has 6 characters
    10 points - 4 items (2 pairs @ 5 points each)
     3 Points - "Emils Cheese Pizza" is 18 characters (a multiple of 3)
                item price of 12.25 * 0.2 = 2.45, rounded up is 3 points
     3 Points - "Klarbrunn 12-PK 12 FL OZ" is 24 characters (a multiple of 3)
                item price of 12.00 * 0.2 = 2.4, rounded up is 3 points
     6 points - purchase day is odd
  + ---------
  = 28 points
```

----

```json
{
  "retailer": "M&M Corner Market",
  "purchaseDate": "2022-03-20",
  "purchaseTime": "14:33",
  "items": [
    {
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    }
  ],
  "total": "9.00"
}
```

```text
Total Points: 109
Breakdown:
    50 points - total is a round dollar amount
    25 points - total is a multiple of 0.25
    14 points - retailer name (M&M Corner Market) has 14 alphanumeric characters
                note: '&' is not alphanumeric
    10 points - 2:33pm is between 2:00pm and 4:00pm
    10 points - 4 items (2 pairs @ 5 points each)
  + ---------
  = 109 points
```

---

