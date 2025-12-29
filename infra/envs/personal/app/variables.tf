variable "region" {
  default = "eu-west-1"
}

variable "environment" {
  default = "personal"
}

variable "sales_ingester_version" {
  type = string
  default = "0.1.0"
}

variable "sales_cleanup_version" {
  type = string
  default = "0.1.0"
}