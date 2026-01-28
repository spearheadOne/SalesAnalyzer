resource "aws_ecr_repository" "sales_ingester_repo" {
  name = "${var.sales_ingester_app}-${var.environment}"

  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.sales_ingester_app}-${var.environment}"
    Environment = var.environment
  }
}

resource "aws_ecr_repository" "sales_cleanup_repo" {
  name = "${var.sales_cleanup_app}-${var.environment}"

  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.sales_cleanup_app}-${var.environment}"
    Environment = var.environment
  }
}


resource "aws_ecr_repository" "sales_analyzer_job_repo" {
  name = "${var.sales_analyzer_job_app}-${var.environment}"

  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.sales_analyzer_job_app}-${var.environment}"
    Environment = var.environment
  }
}

resource "aws_ecr_repository" "sales_fx_service_repo" {
  name = "${var.sales_fx_service_app}-${var.environment}"

  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.sales_fx_service_app}-${var.environment}"
    Environment = var.environment
  }
}

resource "aws_ecr_repository" "sales_dashboard_repo" {
  name = "${var.sales_dashboard_app}-${var.environment}"

  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.sales_dashboard_app}-${var.environment}"
    Environment = var.environment
  }
}