resource "aws_security_group" "sales_analyzer_job_sg" {
  name = "${var.sales_analyzer_job_app}-sg-${var.environment}"
  description = "SG for Sales Analyzer Job worker"
  vpc_id      = var.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Environment = var.environment
    Service     = var.sales_analyzer_job_app
  }
}