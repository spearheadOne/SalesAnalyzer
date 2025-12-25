resource "aws_sqs_queue" "sales_dlq" {
  name                       = "${var.queue-name}-dead-letter-${var.environment}"
  message_retention_seconds  = 1209600 # 14 days max
}

resource "aws_sqs_queue" "sales_queue" {
  name = "${var.queue-name}-${var.environment}"
  visibility_timeout_seconds = 60
  message_retention_seconds = 1209600   # 14 days

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.sales_dlq.arn
    maxReceiveCount     = 5
  })

  tags = {
    Environment = var.environment
    Service     = "sales"
  }
}