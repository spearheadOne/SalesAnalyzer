FROM public.ecr.aws/lambda/java:21
WORKDIR /var/task
COPY build/libs/*-all.jar /var/task/lib/
CMD ["org.abondar.experimental.sales.analyzer.ingester.input.SalesIngesterHandler"]