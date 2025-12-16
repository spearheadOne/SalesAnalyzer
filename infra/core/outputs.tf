output "sales_bucket_arn" {
  description = "ARN of the sales S3 bucket"
  value       = aws_s3_bucket.sales_data.arn
}

output "sales_bucket_name" {
  description = "Actual created S3 bucket name (globally unique)"
  value       = aws_s3_bucket.sales_data.arn
}
