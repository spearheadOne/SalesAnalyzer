 output "upload_invoke_url" {
  value = module.core.upload_invoke_url
}

output "sales_bucket_name" {
  value = module.core.sales_bucket_name
}

output "sales_bucket_arn" {
  value = module.core.sales_bucket_arn
}


output "sqs_queue_url" {
  value = module.core.sqs_queue_url
}

output "sqs_queue_name" {
  value = module.core.sqs_queue_name
}

output "sqs_queue_arn" {
  value = module.core.sqs_queue_arn
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

output "ecr_registry" {
  value = module.core.ecr_registry
}

output "timescale_secret_arn" {
  value = module.core.timescale_secret_arn
}

output "timescale_host" {
  value = module.core.timescale_host
  sensitive = true
}

output "vpc_id" {
  value     = module.core.vpc_id
  sensitive = true
}

output "vpc_cidr" {
  value = module.core.vpc_cidr
  sensitive = true
}

output "subnet_ids" {
  value = module.core.subnet_ids
  sensitive = true
}

