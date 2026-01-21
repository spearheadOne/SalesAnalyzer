resource "aws_iam_role" "sales_analyzer_job_ecs_task_role" {
  name = "ecs-task-${var.sales_analyzer_job_app}-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect    = "Allow",
      Principal = { Service = "ecs-tasks.amazonaws.com" },
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy" "sales_analyzer_job_policy" {
  role = aws_iam_role.sales_analyzer_job_ecs_task_role.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "sqs:SendMessage",
          "sqs:GetQueueAttributes",
          "sqs:GetQueueUrl"
        ],
        Resource = var.sqs_queue_arn
      },

      {
        Effect = "Allow"
        Action = [
          "kinesis:DescribeStream",
          "kinesis:DescribeStreamSummary",
          "kinesis:GetRecords",
          "kinesis:GetShardIterator",
          "kinesis:ListShards",
          "kinesis:ListStreams",
          "kinesis:SubscribeToShard"
        ]
        Resource = var.kinesis_stream_arn
      },

      {
        Effect = "Allow"
        Action = [
          "dynamodb:CreateTable",
          "dynamodb:DescribeTable",
          "dynamodb:UpdateTable",
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Scan",
          "dynamodb:Query"
        ]
        Resource = "*"
      },

      {
        Effect = "Allow"
        Action = [
          "cloudwatch:PutMetricData"
        ]
        Resource = "*"
      }
    ]
  })
}


resource "aws_cloudwatch_log_group" "sales_analyzer_job_log" {
  name              = "/fargate/${var.sales_analyzer_job_app}-${var.environment}"
  retention_in_days = 14

  tags = {
    Environment = var.environment
    Service     = var.sales_analyzer_job_app
  }
}


resource "aws_ecs_task_definition" "sales_analyzer_job" {
  family             = "${var.sales_analyzer_job_app}-${var.environment}"
  requires_compatibilities = ["FARGATE"]
  network_mode       = "awsvpc"
  cpu                = var.fargate_cpu
  memory             = var.fargate_memory
  execution_role_arn = aws_iam_role.sales_ecs_execution_role.arn
  task_role_arn      = aws_iam_role.sales_analyzer_job_ecs_task_role.arn
  runtime_platform {
    cpu_architecture        = "ARM64"
    operating_system_family = "LINUX"
  }

  container_definitions = jsonencode([
    {
      name      = var.sales_analyzer_job_app
      image     = var.sales_analyzer_job_image_uri
      essential = true


      environment = [
        { name = "MICRONAUT_ENVIRONMENTS", value = var.micronaut_env },
        { name = "TIMESCALE_HOST", value = var.timescale_host },
        { name = "TIMESCALE_USERNAME", value = var.timescale_username },
        { name = "TIMESCALE_PASSWORD", value = var.timescale_password },
        { name = "KINESIS_STREAM_NAME", value = var.kinesis_stream_name },
        { name = "SQS_QUEUE_URL", value = var.sqs_queue_url },
        { name = "FX_SERVICE_HOST", value = aws_lb.sales_fx_service_nlb.dns_name },
        { name = "FX_SERVICE_PORT", value = tostring( var.fx_service_port) },
        { name = "DEFAULT_CURRENCY", value = var.default_currency }
      ]

      logConfiguration = {
        logDriver = "awslogs",
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.sales_analyzer_job_log.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = var.sales_analyzer_job_app
        }
      }
    }
  ])
}

resource "aws_ecs_service" "sales_analyzer_job" {
  name            = "${var.sales_analyzer_job_app}-${var.environment}"
  cluster         = aws_ecs_cluster.sales_ecs_cluster.name
  task_definition = aws_ecs_task_definition.sales_analyzer_job.arn
  launch_type     = "FARGATE"
  enable_execute_command = true

  network_configuration {
    subnets         = var.subnet_ids
    security_groups = [aws_security_group.sales_dashboard_sg.id]
    assign_public_ip = true
  }
  desired_count   = 1
  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200


  tags = {
    Environment = var.environment
    Service     = var.sales_analyzer_job_app
  }
}