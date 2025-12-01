#!/bin/bash

docker-compose up -d

curl -s http://localhost:4566/_localstack/health | jq

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_REGION=us-east-1

ENDPOINT=http://localhost:4566

if ! aws --endpoint-url="$ENDPOINT" s3 ls s3://sales-bucket 2>/dev/null; then
  echo "Creating S3 bucket..."
  aws --endpoint-url="$ENDPOINT" s3 mb s3://sales-bucket
else
  echo "S3 bucket already exists"
fi

if ! aws --endpoint-url="$ENDPOINT" kinesis describe-stream --stream-name sales-stream 2>/dev/null; then
  echo "Creating Kinesis stream..."
  aws --endpoint-url="$ENDPOINT" kinesis create-stream --stream-name sales-stream --shard-count 1
else
  echo "Kinesis stream already exists"
fi

if ! aws --endpoint-url="$ENDPOINT" sqs get-queue-url --queue-name sales-queue 2>/dev/null; then
  echo "Creating SQS queue..."
  aws --endpoint-url="$ENDPOINT" sqs create-queue --queue-name sales-queue
else
  echo "SQS queue already exists"
fi


echo "Listing resources..."
aws --endpoint-url="$ENDPOINT" s3 ls
aws --endpoint-url="$ENDPOINT" kinesis list-streams
aws --endpoint-url="$ENDPOINT" sqs list-queues