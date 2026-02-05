terraform {
  backend "s3" {
    bucket         = "sales-terraform-states-development"
    key            = "terraform.tfstate"
    region         = "us-east-2"
    dynamodb_table = "sales-terraform-locks-development"
    encrypt        = true
  }
}

data "terraform_remote_state" "core" {
  backend = "s3"
  config = {
    bucket = "sales-terraform-states-development"
    key    = "terraform.tfstate"
    region = "us-east-2"
  }
}