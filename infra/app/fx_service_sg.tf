resource "aws_security_group" "sales_fx_service_sg" {
  name = "${var.sales_fx_service_app}-sg-${var.environment}"
  description = "Security group for sales-fx-service gRPC"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = var.fx_service_port
    to_port     = var.fx_service_port
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }


  tags = {
    Environment = var.environment
    Service     = var.sales_fx_service_app
  }
}
