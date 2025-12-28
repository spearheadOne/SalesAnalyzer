resource "aws_s3_bucket" "sales_data" {
  bucket = "${var.sales_bucket_name}-${var.environment}-${random_id.suffix.hex}"
  tags = {
    Name = "Sales data"
    Environment = var.environment
  }

}


