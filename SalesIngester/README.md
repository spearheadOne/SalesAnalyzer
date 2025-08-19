# Sales Ingestor

Lambda function to read data from S3

## Build and run
```
../gradlew clean build

../ gradlew run
```

### Upload data

curl -X POST "http://localhost:8080/data" \
-H "Content-Type: multipart/form-data" \
-F "data=@sample-data/sample.csv"