resource "aws_instance" "timescale" {
  ami = data.aws_ami.timescale.id
  instance_type = var.db_instance_type

  iam_instance_profile = aws_iam_instance_profile.timescale_ssm_profile.name
  #dev/personal - public
  #uat/prod - private
  subnet_id = var.enable_private_subnets ? aws_subnet.sales_private[0].id : aws_subnet.sales_public[0].id

  vpc_security_group_ids = [aws_security_group.timescale_sg.id]

  root_block_device {
    volume_size = 50
    volume_type = "gp3"
  }

  user_data = templatefile("${path.module}/templates/install_timescale.sh.tmpl", {
    db_name = var.db_name
    db_user = var.db_username
    db_pass = random_password.db_password.result
    vpc_cidr = aws_vpc.sales_vpc.cidr_block
  })

  tags = {
    Name        = "timescale-${var.environment}"
    Environment = var.environment
  }
}