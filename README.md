# Sales Data Analyzer

Tiny sales data analyzer

## Modules

 - [SalesIngestor](SalesIngestor/README.md)
 - [SalesAnalyzerJob](SalesAnalyzerJob/README.md)
 - [SalesDashboard](SalesDashboard/README.md)
 - [SalesFxService](SalesFxService/README.md)
 - [SalesCleanup](SalesCleanup/README.md)
 - [Data](Data/README.md)


## Build and run

```
./gradlew clean build

./local-infra.sh
```
TODO: curl -X POST http://localhost:8080/upload/sample.csv \
-F "data=@./sample-data/sample.csv"

TODO:  curl -X PUT \
"https://<api-id>.execute-api.<region>.amazonaws.com/personal/upload/sample.csv" \
-H "Content-Type: text/csv" \
--data-binary @sample-data/sample.csv


TODO:  ./build/install/SalesDashboard/bin/SalesDashboard 

TODO:  ./build/native/nativeCompile/SalesIngester
TODO:  export SALESINGESTER_ECR_REPO=<ecr repo url>
TODO: aws ecr get-login-password --region eu-west-1 \
| docker login --username AWS --password-stdin <ecr-repo>