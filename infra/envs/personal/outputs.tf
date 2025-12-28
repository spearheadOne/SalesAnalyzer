output "upload_invoke_url" {
  value = module.core.upload_invoke_url
}

output "sales_bucket_name" {
  value = module.core.sales_bucket_name
}

output "sqs_queue_url" {
  value = module.core.sqs_queue_url
}

output "sqs_queue_name" {
  value = module.core.sqs_queue_name
}

output "kinesis_stream_name" {
  value = module.core.kinesis_stream_name
}