resource "aws_vpc" "sales_vpc" {
  cidr_block = var.vpc_cidr
  enable_dns_support = true
  enable_dns_hostnames = true

  tags = {
    Name = "sales-vpc-${var.environment}"
    Environment = var.environment
  }

}

resource "aws_internet_gateway" "sales_igw" {
  vpc_id = aws_vpc.sales_vpc.id

  tags = {
    Name = "sales-igw-${var.environment}"
    Environment = var.environment
  }
}

resource "aws_subnet" "sales_public" {
  count = var.number_of_azs
  vpc_id = aws_vpc.sales_vpc.id

  #cidr_block = "10.0.0.0/24"
  cidr_block = cidrsubnet(aws_vpc.sales_vpc.cidr_block, 8, count.index)

  availability_zone = "${var.region}${["a","b","c"][count.index]}"
  map_public_ip_on_launch = true

  tags = {
    Name = "sales-subnet-${var.environment}-${count.index}"
    Environment = var.environment
  }
}

resource "aws_route_table" "sales_rt" {
  vpc_id = aws_vpc.sales_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.sales_igw.id
  }

  tags = {
    Name = "sales-rt-${var.environment}"
    Environment = var.environment
  }
}

resource "aws_route_table_association" "sales_rt_a" {
  count = var.number_of_azs
  subnet_id = aws_subnet.sales_public[count.index].id
  route_table_id = aws_route_table.sales_rt.id
}

resource "aws_subnet" "sales_private" {
  count = var.enable_private_subnets ? 1 : 0

  vpc_id            = aws_vpc.sales_vpc.id
  cidr_block = cidrsubnet(aws_vpc.sales_vpc.cidr_block, 8, 128 + count.index)
  availability_zone = "${var.region}${["a","b","c"][count.index]}"

  tags = {
    Name        = "sales-private-db-${var.environment}"
    Environment = var.environment
  }
}

##TODO: add nat and private app subnets for PROD