variable "region" {
  default = "eu-west-1"
}

variable "environment" {
  default = "development"
}

variable "db_instance_type" {
  default = "t3.medium"
}

variable "enable_private_subnets" {
  default = false
}

variable "number_of_azs" {
  default = 2
}

variable "vpc_cidr" {
  type = string
  default = "10.0.0.0/16"
}