resource "aws_api_gateway_rest_api" "upload_api" {
  name = "data_upload"
  description = "Upload API"
}

resource "aws_api_gateway_resource" "upload_resource" {
  rest_api_id = aws_api_gateway_rest_api.upload_api.id
  parent_id = aws_api_gateway_rest_api.upload_api.root_resource_id
  path_part = "upload"
}

resource "aws_api_gateway_resource" "filename_resource" {
  rest_api_id = aws_api_gateway_rest_api.upload_api.id
  parent_id = aws_api_gateway_resource.upload_resource.id
  path_part = "{filename}"
}

resource "aws_api_gateway_method" "upload_put_method" {
  rest_api_id = aws_api_gateway_rest_api.upload_api.id
  resource_id = aws_api_gateway_resource.filename_resource.id
  http_method = "PUT"
  authorization = "NONE"
  request_parameters = {
    "method.request.path.filename" = true
  }
}

resource "aws_api_gateway_method_settings" "all" {
  rest_api_id = aws_api_gateway_rest_api.upload_api.id
  stage_name  = aws_api_gateway_stage.stage.stage_name
  method_path = "*/*"

  settings {
    logging_level      = "INFO"
    data_trace_enabled = true
    metrics_enabled    = true
  }
}

resource "aws_api_gateway_integration" "upload_to_s3" {
  rest_api_id = aws_api_gateway_rest_api.upload_api.id
  resource_id = aws_api_gateway_resource.filename_resource.id
  http_method = aws_api_gateway_method.upload_put_method.http_method
  credentials             = aws_iam_role.upload_s3_role.arn
  integration_http_method = "PUT"
  type = "AWS"
  uri = "arn:aws:apigateway:${var.region}:s3:path/${aws_s3_bucket.sales_data.bucket}/{filename}"

  request_parameters = {
    "integration.request.path.filename" = "method.request.path.filename"
  }

  request_templates = {
    "text/csv"              = "$input.body"
    "application/octet-stream" = "$input.body"
    "application/json"      = "$input.body"
  }

  passthrough_behavior = "WHEN_NO_TEMPLATES"
}

resource "aws_api_gateway_deployment" "upload_deployment" {
  rest_api_id = aws_api_gateway_rest_api.upload_api.id

  depends_on = [
    aws_api_gateway_integration.upload_to_s3
  ]

  lifecycle {
    create_before_destroy = true
  }

  variables = {
    deployed_at = timestamp()
  }
}

resource "aws_api_gateway_method_response" "upload_200" {
  rest_api_id = aws_api_gateway_rest_api.upload_api.id
  resource_id = aws_api_gateway_resource.filename_resource.id
  http_method = aws_api_gateway_method.upload_put_method.http_method
  status_code = "200"
}

resource "aws_api_gateway_integration_response" "upload_int_200" {
  rest_api_id = aws_api_gateway_rest_api.upload_api.id
  resource_id = aws_api_gateway_resource.filename_resource.id
  http_method = aws_api_gateway_method.upload_put_method.http_method
  status_code = aws_api_gateway_method_response.upload_200.status_code
}


resource "aws_iam_role" "upload_s3_role" {
  name = "apigw-s3-upload-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = { Service = "apigateway.amazonaws.com" },
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy" "apigw_s3_policy" {
  role = aws_iam_role.upload_s3_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Action = ["s3:PutObject"],
      Resource = "${aws_s3_bucket.sales_data.arn}/*"
    }]
  })
}

resource "aws_iam_role" "upload_cloudwatch_role" {
  name = "apigw-cloudwatch-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect    = "Allow",
      Principal = { Service = "apigateway.amazonaws.com" },
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "upload_cloudwatch_attach" {
  role       = aws_iam_role.upload_cloudwatch_role.name
  policy_arn  = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
}

resource "aws_api_gateway_account" "account" {
  cloudwatch_role_arn = aws_iam_role.upload_cloudwatch_role.arn
}

resource "aws_cloudwatch_log_group" "upload_access_log_group" {
  name = "/aws/apigateway/${aws_api_gateway_rest_api.upload_api.name}/${var.environment}"
  retention_in_days = 7
}

resource "aws_api_gateway_stage" "stage" {
  deployment_id = aws_api_gateway_deployment.upload_deployment.id
  rest_api_id = aws_api_gateway_rest_api.upload_api.id
  stage_name = var.environment

  depends_on = [aws_api_gateway_account.account]

  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.upload_access_log_group.arn
    format = jsonencode({
      requestId    = "$context.requestId"
      ip           = "$context.identity.sourceIp"
      requestTime  = "$context.requestTime"
      httpMethod   = "$context.httpMethod"
      resourcePath = "$context.resourcePath"
      status       = "$context.status"
      protocol     = "$context.protocol"
      responseLen  = "$context.responseLength"
      errorMessage = "$context.error.message"
      integrationError = "$context.integration.error"
    })
  }
}
