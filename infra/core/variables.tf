variable "sales_bucket_name" {
  default = "sales-data"
}

variable "environment" {
  type = string
  description = "Environment name (personal/dev/prod)"
}

variable "db_instance_type" {
  type = string
  description = "EC2 instance type for db"
}

variable "enable_private_subnets" {
  type = bool
  description = "are private subnets used(UAT/PROD)?"
}

variable "number_of_azs" {
  type = number
  description = "the number of AZs required(UAT/PROD)"
}

variable "region" {
  default = "eu-west-1"
}

variable "queue_name" {
  default = "sales-queue"
}

variable "kinesis_stream_name" {
  default = "sales-stream"
}

variable "db_name" {
  type    = string
  default = "sales_metrics"
}

variable "db_username" {
  type    = string
  default = "metrics_admin"
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
