output "upload_invoke_url" {
  value = module.core.upload_invoke_url
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

output "aws_region" {
  value = var.region
}

output "ecr_registry" {
  value = module.core.ecr_registry
}