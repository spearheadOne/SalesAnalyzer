variable "sales_bucket_name" {
  default = "sales-data"
}

variable "environment" {
  description = "Environment name (personal/dev/prod)"
}

variable "db_instance_type" {
  description = "EC2 instance type for db"
}

variable "enable_private_subnets" {
  type    = bool
}

variable "number_of_azs" {
  type    = number
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

