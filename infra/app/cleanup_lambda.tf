resource "aws_iam_role" "sales_cleanup_role" {
  name = "${var.sales_cleanup_app}-${var.environment}"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect    = "Allow",
      Principal = { Service = "lambda.amazonaws.com" },
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy" "sales_cleanup_policy" {
  role = aws_iam_role.sales_cleanup_role.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:GetObject",
          "s3:ListBucket",
          "s3:DeleteObject",
          "s3:DeleteObjectVersion"
        ],
        Resource = [
          var.sales_bucket_arn,
          "${var.sales_bucket_arn}/*"
        ]
      },
      {
        Effect = "Allow",
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        Resource = "arn:aws:logs:${var.region}:*:log-group:/aws/lambda/${var.sales_cleanup_app}-*"
      }
    ]
  })
}

resource "aws_lambda_function" "sales_cleanup" {
  function_name = "${var.sales_cleanup_app}-${var.environment}"
  package_type  = "Image"
  image_uri     = var.sales_cleanup_image_uri
  role          = aws_iam_role.sales_cleanup_role.arn

  timeout       = var.lambda_timeout
  memory_size   = var.lambda_memory_size
  architectures = ["arm64"]

  environment {
    variables = {
      SALES_BUCKET_NAME = var.sales_bucket_name
      MICRONAUT_ENVIRONMENTS = "lambda"
    }
  }

  tags = {
    Environment = var.environment
    Service     = var.sales_cleanup_app
  }
}

resource "aws_cloudwatch_event_rule" "sales_cleanup_schedule" {
  name                = "${var.sales_cleanup_app}-schedule-${var.environment}"
  description         = "Nightly cleanup for ${var.sales_cleanup_app} (${var.environment})"
  schedule_expression = "cron(0 1 * * ? *)" # 1:00 UTC every day
}

resource "aws_cloudwatch_event_target" "sales_cleanup_target" {
  rule      = aws_cloudwatch_event_rule.sales_cleanup_schedule.name
  target_id = aws_lambda_function.sales_cleanup.function_name
  arn       = aws_lambda_function.sales_cleanup.arn
}

resource "aws_lambda_permission" "allow_sales_data_cleanup_scheduled" {
  statement_id  = "AllowS3InvokeSalesCleanup-${var.environment}"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.sales_cleanup.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.sales_cleanup_schedule.arn
}

resource "aws_cloudwatch_log_group" "sales_cleanup_log" {
  name              = "/aws/lambda/${aws_lambda_function.sales_cleanup.function_name}"
  retention_in_days = 14

  depends_on = [
    aws_lambda_function.sales_cleanup
  ]

  tags = {
    Service     = var.sales_cleanup_app
    Environment = var.environment
  }
}
