variable "environment" {
  description = "Environment name (personal/dev/prod)"
}

variable "region" {
  type = string
}

variable "lambda_timeout" {
  type = number
}

variable "lambda_memory_size" {
  type = number
}

variable "fargate_cpu" {
  type = number
}

variable "fargate_memory" {
  type = number
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

variable "kinesis_stream_arn" {
  type = string
}


variable "sqs_queue_name" {
  type = string
}

variable "sqs_queue_url" {
  type = string
}

variable "sqs_queue_arn" {
  type = string
}

variable "timescale_host" {
  type = string
}

variable "timescale_username" {
  type = string
}

variable "timescale_password" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "vpc_cidr" {
  type = string
}

variable "subnet_ids" {
  type = set(string)
}

variable "micronaut_env" {
  type = string
  default = "aws"
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

variable "sales_ingester_image_uri" {
  type = string
}

variable "sales_cleanup_image_uri" {
  type = string
}

variable "sales_dashboard_image_uri" {
  type = string
}

variable "sales_fx_service_image_uri" {
  type = string
}

variable "sales_analyzer_job_image_uri" {
  type = string
}

variable "sales_ecs_cluster" {
  type = string
  default = "sales-ecs-cluster"
}

variable "default_currency" {
  type = string
  default = "EUR"
}

variable "fx_service_port" {
  type = number
  default = 9028
}