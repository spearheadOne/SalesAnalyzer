terraform {
  backend "local" {
    path = "terraform-personal-app.tfstate"
  }
}

data "terraform_remote_state" "core" {
  backend = "local"
  config = {
    path = "../core/terraform-personal-core.tfstate"
  }
}