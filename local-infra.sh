#!/bin/bash

docker-compose up -d

curl -s http://localhost:4566/_localstack/health | jq

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_REGION=us-east-1

ENDPOINT=http://localhost:4566

aws --endpoint-url="$ENDPOINT" s3 mb s3://sales-bucket
aws --endpoint-url="$ENDPOINT" kinesis create-stream --stream-name sales-stream --shard-count 1
aws --endpoint-url="$ENDPOINT" s3 ls
aws --endpoint-url="$ENDPOINT" kinesis list-streams
