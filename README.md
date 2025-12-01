# Sales Data Analyzer

Tiny sales data analyzer

## Modules

 - [SalesIngestor](SalesIngestor/README.md)
 - [SalesAnalyzerJob](SalesAnalyzerJob/README.md)
 - [SalesDashboard](SalesDashboard/README.md)
 - [SalesFxService](SalesFxService/README.md)


## Build and run

```
./gradlew clean build

./local-infra.sh
```
TODO: curl -X POST http://localhost:8080/data \
-F "data=@./sample-data/sample.csv"


TODO:  ./build/install/SalesDashboard/bin/SalesDashboard 

TODO:  ./build/native/nativeCompile/SalesIngester