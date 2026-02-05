terraform {
  backend "s3" {
    bucket         = "sales-terraform-states-development"
    key            = "core/terraform.tfstate"
    region         = "us-east-2"
    dynamodb_table = "sales-terraform-locks-development"
    encrypt        = true
  }
}
