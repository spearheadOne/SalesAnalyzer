resource "aws_security_group" "sales_dashboard_sg" {
  name = "${var.sales_dashboard_app}-sg-${var.environment}"
  description = "Allow HTTP from ALB to sales-dashboard"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 9024
    to_port     = 9024
    protocol    = "tcp"
    security_groups = [aws_security_group.sales_dashboard_alb_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }


  tags = {
    Environment = var.environment
    Service     = var.sales_dashboard_app
  }
}

resource "aws_security_group" "sales_dashboard_alb_sg" {
  name        = "${var.sales_dashboard_app}-alb-${var.environment}"
  description = "Public access to ${var.sales_dashboard_app} ALB"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Environment = var.environment
    Service     = var.sales_dashboard_app
  }
}

resource "aws_lb" "sales_dashboard" {
  name        = "${var.sales_dashboard_app}-alb-${var.environment}"
  load_balancer_type = "application"
  subnets            = var.subnet_ids
  security_groups    = [aws_security_group.sales_dashboard_alb_sg.id]

  tags = {
    Environment = var.environment
    Service     = var.sales_dashboard_app
  }
}

resource "aws_lb_target_group" "sales_dashboard" {
  name        = "${var.sales_dashboard_app}-${var.environment}"
  port        = 9024
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = var.vpc_id

  health_check {
    path                = "/health"
    protocol            = "HTTP"
    matcher             = "200"
    interval            = 30
    healthy_threshold   = 2
    unhealthy_threshold = 3
  }

  tags = {
    Environment = var.environment
    Service     = var.sales_dashboard_app
  }
}

resource "aws_lb_listener" "sales_dashboard_http" {
  load_balancer_arn = aws_lb.sales_dashboard.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.sales_dashboard.arn
  }
}