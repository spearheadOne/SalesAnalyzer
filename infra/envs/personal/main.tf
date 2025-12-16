module "core" {
  source = "../../core"
  environment = var.environment
}

module "app" {
  source = "../../app"
  environment = var.environment
}