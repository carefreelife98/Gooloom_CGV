terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "=3.42.0"
    }
  }
}

provider "aws" {
  region  = var.region
  access_key = var.AWS_ACCESS_KEY_ID
  secret_key = var.AWS_SECRET_ACCESS_KEY
}

resource "aws_vpc" "vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true

  tags = {
    name = "${var.prefix}-CGV-vpc-${var.region}"
#    environment = "Production"
  }
}

resource "aws_subnet" "public_subnet" {
  count             = 2 # 두번 반복
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, (count.index * 1) + 5)
  availability_zone = element(var.azs, count.index)
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.prefix}-${var.env}-sub-pub-${count.index}"
    "kubernetes.io/cluster/lastDance${count.index}" = "shared"
    "kubernetes.io/role/elb" = "1"
  }
}

resource "aws_subnet" "private_subnet-3a" {
  count             = 3
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, (count.index * 1) + 11)
  availability_zone = var.azs[0]
  map_public_ip_on_launch = false #퍼블릭 IP 부여를 하지 않습니다.
  tags = {
    Name = "${var.prefix}-${var.env}-sub-${element(var.svc, count.index)}-pri-3a"
    "kubernetes.io/cluster/lastDance${count.index}" = "shared"
    "kubernetes.io/role/internal-elb" = "1"
  }
}

resource "aws_subnet" "private_subnet-3c" {
  count             = 3
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, (count.index * 1) + 22)
  availability_zone = var.azs[1]
  map_public_ip_on_launch = false #퍼블릭 IP 부여를 하지 않습니다.
  tags = {
    Name = "${var.prefix}-${var.env}-sub-${element(var.svc, count.index)}-pri-3c"
    "kubernetes.io/cluster/lastDance${count.index}" = "shared"
    "kubernetes.io/role/internal-elb" = "1"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "${var.prefix}-internet-gateway"
  }
}

resource "aws_route_table" "rt" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
}

#resource "aws_lb" "alb" {
#  name               = "${var.prefix}-${var.env}-CGV-alb"
#  internal           = false
#  load_balancer_type = "application"
#  subnets            = aws_subnet.public_subnet[*].id
#
#  enable_deletion_protection = false
#}
#
#resource "aws_lb_listener" "alb_listener" {
#  load_balancer_arn = aws_lb.alb.arn
#  port              = 80
#  protocol          = "HTTP"
#
#  default_action {
#    type = "fixed-response"
#
#    fixed_response {
#      content_type = "text/plain"
#      message_body = "Hello, world!"
#      status_code  = "200"
#    }
#  }
#}

resource "aws_route_table" "public_subnet_rt" {
  count = 2
  vpc_id = aws_vpc.vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id #Internet Gateway 별칭 입력
  }
  tags = {
    Name = "${var.prefix}-${var.env}-public-subnet-rt-${count.index}"
  }
}

resource "aws_route_table_association" "public_subnet_assoc" {
  count       = 2
  subnet_id   = aws_subnet.public_subnet[count.index].id
  route_table_id = aws_route_table.public_subnet_rt[count.index].id
}

# 아래 내용은 위의 route {} 로 포함됨
#resource "aws_route" "public_subnet_rt_association" {
#  count = 2
#  route_table_id         = aws_route_table.public_subnet_rt[count.index].id
#  destination_cidr_block = "0.0.0.0/0"
#  gateway_id             = aws_internet_gateway.igw.id
#}
#

############### private routing ###############

resource "aws_route_table" "private_subnet_rt_3a" {
  count = 3
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "${var.prefix}-${var.env}-private-subnet-rt-3a-${count.index}"
  }
}
resource "aws_route_table_association" "private_subnet_3a_assoc" {
  count       = 3
  subnet_id   = aws_subnet.private_subnet-3a[count.index].id
  route_table_id = aws_route_table.private_subnet_rt_3a[count.index].id
}
resource "aws_route" "private_subnet_rt_3a_association" {
  count = 2
  route_table_id         = aws_route_table.private_subnet_rt_3a[count.index].id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.nat_gw_3a[0].id
}


resource "aws_route_table" "private_subnet_rt_3c" {
  count = 3
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "${var.prefix}-${var.env}-private-subnet-rt-3c-${count.index}"
  }
}

resource "aws_route_table_association" "private_subnet_3c_assoc" {
  count       = 3
  subnet_id   = aws_subnet.private_subnet-3c[count.index].id
  route_table_id = aws_route_table.private_subnet_rt_3c[count.index].id
}

resource "aws_route" "private_subnet_rt_3c_association" {
  count = 2
  route_table_id         = aws_route_table.private_subnet_rt_3c[count.index].id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.nat_gw_3c[0].id
}



############### NAT Instance & Keypair ###############
#resource "aws_instance" "bastion" {
#  count      = 1
#  ami        = "ami-01056eaaa603955a4"  # 이 부분은 실제 AMI ID로 변경해야 합니다.
#  instance_type = "t3.medium"
#  subnet_id  = aws_subnet.public_subnet[0].id
#  key_name = "gooloom"
#  tags = {
#    Name = "${var.prefix}-${var.env}-bastion-3a"
#  }
#}

#resource "aws_instance" "nat_instance_3a" {
#  count      = 1
#  ami        = "ami-01056eaaa603955a4"  # 이 부분은 실제 AMI ID로 변경해야 합니다.
#  instance_type = "t3.medium"
#  subnet_id  = aws_subnet.public_subnet[0].id
#  tags = {
#    Name = "${var.prefix}-${var.env}-nat-instance-3a"
#  }
#}

#resource "aws_instance" "nat_instance_3c" {
#  count      = 1
#  ami        = "ami-01056eaaa603955a4"  # 이 부분은 실제 AMI ID로 변경해야 합니다.
#  instance_type = "t3.medium"
#  subnet_id  = aws_subnet.public_subnet[1].id
#  tags = {
#    Name = "${var.prefix}-${var.env}-nat-instance-3c"
#  }
#}





resource "aws_nat_gateway" "nat_gw_3a" {
  count      = 1
  allocation_id = aws_eip.nat_eip_3a[count.index].id
  subnet_id  = aws_subnet.public_subnet[0].id

  tags = {
    Name = "${var.prefix}-${var.env}-nat-gw-3a"
  }
}

resource "aws_nat_gateway" "nat_gw_3c" {
  count      = 1
  allocation_id = aws_eip.nat_eip_3c[count.index].id
  subnet_id  = aws_subnet.public_subnet[1].id

  tags = {
    Name = "${var.prefix}-${var.env}-nat-gw-3c"
  }
}

resource "aws_eip" "nat_eip_3a" {
  count = 1

  tags = {
    Name = "${var.prefix}-${var.env}-nat-eip-3a"
  }
}

resource "aws_eip" "nat_eip_3c" {
  count = 1

  tags = {
    Name = "${var.prefix}-${var.env}-nat-eip-3c"
  }
}