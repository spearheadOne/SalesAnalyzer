resource "aws_iam_role" "sales_ingester_role" {
  name = "${var.sales_ingester_app}-${var.environment}"
  assume_role_policy =  jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = { Service = "lambda.amazonaws.com" },
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy" "sales_ingester_policy" {
  role = aws_iam_role.sales_ingester_role.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:GetObject",
          "s3:ListBucket"
        ],
        Resource = [
          var.sales_bucket_arn,
          "${var.sales_bucket_arn}/*"
        ]
      },
      {
        Effect = "Allow",
        Action = [
          "kinesis:PutRecord",
          "kinesis:PutRecords"
        ],
        Resource = "arn:aws:kinesis:${var.region}:${data.aws_caller_identity.current.account_id}:stream/${var.kinesis_stream_name}"
      },
      {
        Effect = "Allow",
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        Resource = "arn:aws:logs:${var.region}:*:log-group:/aws/lambda/${var.sales_ingester_app}-*"
      }
    ]
  })
}

resource "aws_lambda_function" "sales_ingester" {
  function_name = "${var.sales_ingester_app}-${var.environment}"
  package_type = "Image"
  image_uri = var.sales_ingester_image_uri
  role          = aws_iam_role.sales_ingester_role.arn
  architectures = ["arm64"]

  timeout     = var.lambda_timeout
  memory_size = var.lambda_memory_size

  environment {
    variables = {
      SALES_BUCKET_NAME = var.sales_bucket_name
      KINESIS_STREAM_NAME = var.kinesis_stream_name
    }
  }

  tags = {
    Environment = var.environment
    Service     = var.sales_ingester_app
  }
}

resource "aws_lambda_permission" "allow_sales_data_call_ingester" {
  statement_id = "AllowS3InvokeSalesIngester-${var.environment}"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.sales_ingester.function_name
  principal     = "s3.amazonaws.com"
  source_arn = var.sales_bucket_arn
}

resource "aws_s3_bucket_notification" "sales_data_upload" {
  bucket = var.sales_bucket_name
  lambda_function {
    lambda_function_arn = aws_lambda_function.sales_ingester.arn
    events = ["s3:ObjectCreated:*"]
  }

  depends_on = [
    aws_lambda_permission.allow_sales_data_call_ingester
  ]
}

resource "aws_cloudwatch_log_group" "sales_ingester_log" {
  name              = "/aws/lambda/${aws_lambda_function.sales_ingester.function_name}"
  retention_in_days = 14

  depends_on = [
    aws_lambda_function.sales_ingester
  ]

  tags = {
    Service     = var.sales_cleanup_app
    Environment = var.environment
  }
}