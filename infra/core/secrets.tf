resource "aws_secretsmanager_secret" "sales_data_credentials" {
  name = "sales-data-credentials-${var.environment}"

  tags = {
    Environment = var.environment
    Service     = "timescaledb"
  }
}

resource "aws_secretsmanager_secret_version" "sales_data_credentials_version" {
  secret_id = aws_secretsmanager_secret.sales_data_credentials.id
  secret_string = jsonencode({
    username = var.db_username
    password = random_password.db_password.result
    dbname   = var.db_name
  })
}