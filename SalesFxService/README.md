# Sales FX Service

Tiny service to do currency conversion. Right now manually converts several currencies (USD,GBP,CHF,JPY,UAH,RUB) to EUR.

In reality, integration with external exchange rates API should be done. Exposes GRPC server used by analyzer job.

## Run locally
```
../gradlew run
```
