# Sales Analyzer Function



## Build and run
```
../gradlew clean build

../ gradlew run
```

### Upload data

curl -X POST "http://localhost:8080/data" \
-H "Content-Type: multipart/form-data" \
-F "data=@sample-data/sample.csv"