variable "environment" {
  description = "Environment name (personal/dev/prod)"
}

variable "region" {
  type = string
}

variable "sales_ingester_image_uri" {
  type = string
}

variable "sales_cleanup_image_uri" {
  type = string
}


variable "sales_bucket_name" {
  type = string
}

variable "sales_bucket_arn" {
  type = string
}

variable "kinesis_stream_name" {
  type = string
}

variable "sales_ingester_app" {
  type    = string
  default = "sales-ingester"
}

variable "sales_cleanup_app" {
  type    = string
  default = "sales-cleanup"
}

variable "sales_analyzer_job_app" {
  type    = string
  default = "sales-analyzer-job"
}

variable "sales_fx_service_app" {
  type    = string
  default = "sales-fx-service"
}

variable "sales_dashboard_app" {
  type    = string
  default = "sales-dashboard"
}
