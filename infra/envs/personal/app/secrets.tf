data "aws_secretsmanager_secret_version" "timescale_creds" {
  secret_id = data.terraform_remote_state.core.outputs.timescale_secret_arn
}

locals {
  timescale_creds = jsondecode(data.aws_secretsmanager_secret_version.timescale_creds.secret_string)
}