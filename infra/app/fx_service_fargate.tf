resource "aws_iam_role" "sales_fx_service_ecs_task_role" {
  name = "ecs-task-${var.sales_fx_service_app}-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect    = "Allow",
      Principal = { Service = "ecs-tasks.amazonaws.com" },
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_cloudwatch_log_group" "sales_fx_service_log" {
  name              = "/fargate/${var.sales_fx_service_app}-${var.environment}"
  retention_in_days = 14

  tags = {
    Environment = var.environment
    Service     = var.sales_fx_service_app
  }
}


resource "aws_ecs_task_definition" "sales_fx_service" {
  family                   = "${var.sales_fx_service_app}-${var.environment}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.fargate_cpu
  memory                   = var.fargate_memory
  execution_role_arn       = aws_iam_role.sales_ecs_execution_role.arn
  task_role_arn            = aws_iam_role.sales_fx_service_ecs_task_role.arn
  runtime_platform {
    cpu_architecture        = "ARM64"
    operating_system_family = "LINUX"
  }

  container_definitions = jsonencode([
    {
      name      = var.sales_fx_service_app
      image     = var.sales_fx_service_image_uri
      essential = true

      portMappings = [
        {
          containerPort = var.fx_service_port
          hostPort      = var.fx_service_port
          protocol      = "tcp"
        }
      ]

      environment = [
        { name = "MICRONAUT_SERVER_PORT", value = tostring( var.fx_service_port) },
      ]

      logConfiguration = {
        logDriver = "awslogs",
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.sales_fx_service_log.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = var.sales_fx_service_app
        }
      }
    }
  ])


  tags = {
    Environment = var.environment
    Service     = var.sales_fx_service_app
  }
}

resource "aws_ecs_service" "sales_fx_service" {
  name            = "${var.sales_fx_service_app}-${var.environment}"
  cluster         = aws_ecs_cluster.sales_ecs_cluster.name
  task_definition = aws_ecs_task_definition.sales_fx_service.arn
  launch_type     = "FARGATE"
  enable_execute_command = true


  network_configuration {
    subnets         = var.subnet_ids
    security_groups = [aws_security_group.sales_fx_service_sg.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.sales_fx_service_tg.arn
    container_name   = var.sales_fx_service_app
    container_port   = 9028
  }

  depends_on = [
    aws_lb_listener.sales_fx_service_nlb_listener
  ]


  desired_count   = 1

  tags = {
    Environment = var.environment
    Service     = var.sales_fx_service_app
  }
}
