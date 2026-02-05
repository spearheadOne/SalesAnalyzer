# Sales Ingestor

Lambda function to read data from S3 by notification. For local usage a tiny endpoint is used.

## Run locally
```
../ gradlew run
```

### Upload data

- Locally
```
curl -X POST "http://localhost:8080/data" \
-H "Content-Type: multipart/form-data" \
-F "data=@sample-data/sample.csv"
``

- In AWS
```
curl -X PUT \
"https://<api-id>.execute-api.<region>.amazonaws.com/personal/upload/sample.csv" \
-H "Content-Type: text/csv" \
--data-binary @sample-data/sample.csv
```