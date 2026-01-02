resource "aws_lb" "sales_fx_service_nlb" {
  name               = "${var.sales_fx_service_app}-nlb-${var.environment}"
  load_balancer_type = "network"
  internal           = true

  subnets = var.subnet_ids

  tags = {
    Environment = var.environment
    Service     = var.sales_fx_service_app
  }
}

resource "aws_lb_target_group" "sales_fx_service_tg" {
  name        = "${var.sales_fx_service_app}-tg-${var.environment}"
  port        = 9028
  protocol    = "TCP"
  target_type = "ip"
  vpc_id      = var.vpc_id

  health_check {
    protocol            = "TCP"
    port                = "9028"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    interval            = 30
  }

  tags = {
    Environment = var.environment
    Service     = var.sales_fx_service_app
  }
}

resource "aws_lb_listener" "sales_fx_service_nlb_listener" {
  load_balancer_arn = aws_lb.sales_fx_service_nlb.arn
  port              = 9028
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.sales_fx_service_tg.arn
  }

  tags = {
    Environment = var.environment
    Service     = var.sales_fx_service_app
  }
}