output "upload_invoke_url" {
  description = "API gateway URL for data upload"
  value = "https://${aws_api_gateway_rest_api.upload_api.id}.execute-api.${var.region}.amazonaws.com/${aws_api_gateway_stage.stage.stage_name}/upload/{filename}"
}

output "sales_ingester_ecr_url" {
  value = aws_ecr_repository.sales_ingester_repo.repository_url
}

output "sales_cleanup_ecr_url" {
  value = aws_ecr_repository.sales_cleanup_repo.repository_url
}

output "sales_analyzer_job_ecr_url" {
  value = aws_ecr_repository.sales_analyzer_job_repo.repository_url
}

output "sales_fx_service_ecr_url" {
  value = aws_ecr_repository.sales_fx_service_repo.repository_url
}

output "sales_dashboard_ecr_url" {
  value = aws_ecr_repository.sales_dashboard_repo.repository_url
}


output "ecr_registry" {
  value = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.region}.amazonaws.com"
}