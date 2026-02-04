variable "region" {
  default = "eu-west-1"
}

variable "environment" {
  default = "personal"
}

variable "lambda_memory_size" {
  default = 512
}

variable "lambda_timeout" {
  default = 60
}

variable "fargate_cpu" {
  default = 512
}

variable "fargate_memory" {
  default = 512
}

variable "sales_analyzer_version" {
  type = string
  default = "0.9.0"
}