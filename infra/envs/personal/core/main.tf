module "core" {
  source = "../../../core"
  environment = var.environment
  db_instance_type = var.db_instance_type
  enable_private_subnets = var.enable_private_subnets
  number_of_azs = var.number_of_azs
}

module "app" {
  source = "../../../app"
  environment = var.environment
}