resource "random_id" "suffix" {
  byte_length = 4
}

resource "random_password" "db_password" {
  length  = 20
  special = false
}
