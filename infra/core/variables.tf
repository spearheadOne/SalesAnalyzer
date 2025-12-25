variable "sales_bucket_name" {
  default = "sales-data"
}

variable "environment" {
  description = "Environment name (personal/dev/prod)"
}

variable "region" {
  default = "eu-west-1"
}

variable "queue-name" {
  default = "sales-queue"
}