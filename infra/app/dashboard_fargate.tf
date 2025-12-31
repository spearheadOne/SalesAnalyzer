resource "aws_iam_role" "sales_dashboard_ecs_task_role" {
  name = "ecs-task-${var.sales_dashboard_app}-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect    = "Allow",
      Principal = { Service = "ecs-tasks.amazonaws.com" },
      Action    = "sts:AssumeRole"
    }]
  })
}


resource "aws_iam_role_policy" "sales_dashboard_policy" {
  role = aws_iam_role.sales_dashboard_ecs_task_role.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "sqs:SendMessage",
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ],
        Resource = var.sqs_queue_arn
      }
    ]
  })
}

resource "aws_cloudwatch_log_group" "sales_dashboard_log" {
  name              = "/fargate/${var.sales_dashboard_app}-${var.environment}"
  retention_in_days = 14

  tags = {
    Environment = var.environment
    Service     = var.sales_dashboard_app
  }
}

resource "aws_ecs_task_definition" "sales_dashboard" {
  family                   = "${var.sales_dashboard_app}-${var.environment}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.fargate_cpu
  memory                   = var.fargate_memory
  execution_role_arn       = aws_iam_role.sales_ecs_execution_role.arn
  task_role_arn            = aws_iam_role.sales_dashboard_ecs_task_role.arn
  runtime_platform {
    cpu_architecture        = "ARM64"
    operating_system_family = "LINUX"
  }

  container_definitions = jsonencode([
    {
      name      = var.sales_dashboard_app
      image     = var.sales_dashboard_image_uri
      essential = true

      portMappings = [
        {
          containerPort = 9024
          hostPort      = 9024
          protocol      = "tcp"
        }
      ]

      environment = [
        { name = "MICRONAUT_ENVIRONMENTS", value = var.micronaut_env },
        { name = "TIMESCALE_HOST", value = var.timescale_host },
        { name = "TIMESCALE_USERNAME", value = var.timescale_username },
        { name = "TIMESCALE_PASSWORD", value = var.timescale_password },
        { name = "SQS_QUEUE_URL", value = var.sqs_queue_url },
        { name = "SQS_QUEUE_NAME", value = var.sqs_queue_name }
      ]

      logConfiguration = {
        logDriver = "awslogs",
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.sales_dashboard_log.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = var.sales_dashboard_app
        }
      }
    }
  ])


  tags = {
    Environment = var.environment
    Service     = var.sales_dashboard_app
  }
}

resource "aws_ecs_service" "sales_dashboard" {
  name            = "${var.sales_dashboard_app}-${var.environment}"
  cluster         = aws_ecs_cluster.sales_ecs_cluster.name
  task_definition = aws_ecs_task_definition.sales_dashboard.arn
  launch_type     = "FARGATE"
  enable_execute_command = true
  network_configuration {
    subnets         = var.subnet_ids
    security_groups = [aws_security_group.sales_dashboard_sg.id]
    assign_public_ip = true
  }
  desired_count   = 1

  load_balancer {
    target_group_arn = aws_lb_target_group.sales_dashboard.arn
    container_name   = var.sales_dashboard_app
    container_port   = 9024
  }

  depends_on = [
    aws_lb_listener.sales_dashboard_http
  ]

  tags = {
    Environment = var.environment
    Service     = "sales-dashboard"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_exec_policy" {
  role       = aws_iam_role.sales_dashboard_ecs_task_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}