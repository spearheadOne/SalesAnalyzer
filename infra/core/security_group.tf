resource "aws_security_group" "sales_data_sg" {
  name        = "sales-data-${var.environment}"
  description = "Sales data db access"
  vpc_id      = aws_vpc.sales_vpc.id

  # access db inside the subnet - inbound to db from apps
  ingress {
       from_port = 5432
       to_port = 5432
       protocol = "tcp"
       cidr_blocks = [aws_vpc.sales_vpc.cidr_block]
  }

  ## allow outbound packets from db to app
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "sales-data-sg-${var.environment}"
    Environment = var.environment
  }
}