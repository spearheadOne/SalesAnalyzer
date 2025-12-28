variable "region" {
  default = "eu-west-1"
}

variable "environment" {
  default = "personal"
}

variable "db_instance_type" {
  default = "t3.medium"
}

variable "enable_private_subnets" {
  default = false
}

variable "number_of_azs" {
  default = 1
}