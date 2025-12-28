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

output "sales_ingester_ecr_url" {
  value = module.core.sales_ingester_ecr_url
}

output "sales_cleanup_ecr_url" {
  value = module.core.sales_cleanup_ecr_url
}

output "sales_analyzer_job_ecr_url" {
  value = module.core.sales_analyzer_job_ecr_url
}

output "sales_fx_service_ecr_url" {
  value = module.core.sales_fx_service_ecr_url
}

output "sales_dashboard_ecr_url" {
  value = module.core.sales_dashboard_ecr_url
}