resource "aws_ecs_cluster" "sales_ecs_cluster" {
  name = "${var.sales_ecs_cluster}-${var.environment}"

  tags = {
    Environment = var.environment
    Service = var.sales_ecs_cluster
  }
}

resource "aws_iam_role" "sales_ecs_execution_role" {
  name = "${var.sales_ecs_cluster}-${var.environment}"
  assume_role_policy =  jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = { Service = "ecs-tasks.amazonaws.com" },
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_policy_attachment" "sales_ecs_execution_role_policy" {
  name = "${var.sales_ecs_cluster}-ecs-exec-role-policy"
  roles= [aws_iam_role.sales_ecs_execution_role.name]
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}