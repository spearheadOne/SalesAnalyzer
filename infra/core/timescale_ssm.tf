resource "aws_iam_role" "timescale_ssm_role" {
  name = "timescale-ssm-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = { Service = "ec2.amazonaws.com" },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

  resource "aws_iam_role_policy_attachment" "timescale_ssm_attach" {
    role       = aws_iam_role.timescale_ssm_role.name
    policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
  }

resource "aws_iam_instance_profile" "timescale_ssm_profile" {
  name = "timescale-ssm-${var.environment}"
  role = aws_iam_role.timescale_ssm_role.name
}