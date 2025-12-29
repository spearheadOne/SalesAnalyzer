module "app" {
  source                   = "../../../app"
  environment              = var.environment
  region                   = var.region
  sales_bucket_name        = data.terraform_remote_state.core.outputs.sales_bucket_name
  sales_bucket_arn         = data.terraform_remote_state.core.outputs.sales_bucket_arn
  kinesis_stream_name      = data.terraform_remote_state.core.outputs.kinesis_stream_name
  sales_ingester_image_uri = "${data.terraform_remote_state.core.outputs.sales_ingester_ecr_url}:${var.sales_ingester_version}"
}
