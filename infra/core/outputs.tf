output "sales_bucket_name" {
  description = "Actual created S3 bucket name (globally unique)"
  value       = aws_s3_bucket.sales_data.bucket
}

output "upload_invoke_url" {
  description = "API gateway URL for data upload"
  value = "https://${aws_api_gateway_rest_api.upload_api.id}.execute-api.${var.region}.amazonaws.com/${aws_api_gateway_stage.stage.stage_name}/upload/{filename}"
}

output "sqs_queue_url" {
  value = aws_sqs_queue.sales_queue.url
}

output "sqs_queue_name" {
  value = aws_sqs_queue.sales_queue.name
}

output "kinesis_stream_name" {
  value = aws_kinesis_stream.sales_stream.name
}