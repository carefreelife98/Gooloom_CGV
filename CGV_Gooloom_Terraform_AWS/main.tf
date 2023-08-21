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
    environment = "Production"
  }
}

resource "aws_subnet" "public_subnet" {
  count             = 2 # 두번 반복
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index)
  availability_zone = element(var.azs, count.index)
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.prefix}-${var.env}-sub-pub-${count.index}"
  }
}

resource "aws_subnet" "private_subnet-2a" {
  count             = 3
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, (count.index * 1) + 11)
  availability_zone = var.azs[0]

  tags = {
    Name = "${var.prefix}-${var.env}-sub-${element(var.svc, count.index)}-pri-2a"
  }
}

resource "aws_subnet" "private_subnet-2c" {
  count             = 3
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, (count.index * 1) + 22)
  availability_zone = var.azs[1]

  tags = {
    Name = "${var.prefix}-${var.env}-sub-${element(var.svc, count.index)}-pri-2c"
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

resource "aws_lb" "alb" {
  name               = "${var.prefix}-${var.env}-CGV-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = aws_subnet.public_subnet[*].id

  enable_deletion_protection = false
}

resource "aws_lb_listener" "alb_listener" {
  load_balancer_arn = aws_lb.alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Hello, world!"
      status_code  = "200"
    }
  }
}
#data "aws_ami" "ubuntu" {
#  most_recent = true
#
#  filter {
#    name = "name"
#    #values = ["ubuntu/images/hvm-ssd/ubuntu-disco-19.04-amd64-server-*"]
#    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
#  }
#
#  filter {
#    name   = "virtualization-type"
#    values = ["hvm"]
#  }
#
#  owners = ["099720109477"] # Canonical
#}

#resource "aws_eip" "eip" {
#  instance = aws_instance.hashicat.id
#}

#resource "aws_eip_association" "hashicat" {
#  instance_id   = aws_instance.hashicat.id
#  allocation_id = aws_eip.hashicat.id
#}

#resource "aws_instance" "hashicat" {
#  ami                         = data.aws_ami.ubuntu.id
#  instance_type               = var.instance_type
#  key_name                    = aws_key_pair.hashicat.key_name
#  associate_public_ip_address = true
#  subnet_id                   = aws_subnet.hashicat.id
#  vpc_security_group_ids      = [aws_security_group.hashicat.id]
#
#  tags = {
#    Name = "${var.prefix}-hashicat-instance"
#  }
#}

# We're using a little trick here so we can run the provisioner without
# destroying the VM. Do not do this in production.

# If you need ongoing management (Day N) of your virtual machines a tool such
# as Chef or Puppet is a better choice. These tools track the state of
# individual files and can keep them in the correct configuration.

# Here we do the following steps:
# Sync everything in files/ to the remote VM.
# Set up some environment variables for our script.
# Add execute permissions to our scripts.
# Run the deploy_app.sh script.

#resource "null_resource" "configure-cat-app" {
#  depends_on = [aws_eip_association.hashicat]
#
#  triggers = {
#    build_number = timestamp()
#  }
#
#  provisioner "file" {
#    source      = "files/"
#    destination = "/home/ubuntu/"
#
#    connection {
#      type        = "ssh"
#      user        = "ubuntu"
#      private_key = tls_private_key.hashicat.private_key_pem
#      host        = aws_eip.hashicat.public_ip
#    }
#  }
#
#  provisioner "remote-exec" {
#    inline = [
#      "sudo apt -y update",
#      "sleep 15",
#      "sudo apt -y update",
#      "sudo apt -y install apache2",
#      "sudo systemctl start apache2",
#      "sudo chown -R ubuntu:ubuntu /var/www/html",
#      "chmod +x *.sh",
#      "PLACEHOLDER=${var.placeholder} WIDTH=${var.width} HEIGHT=${var.height} PREFIX=${var.prefix} ./deploy_app.sh",
#      "sudo apt -y install cowsay",
#      "cowsay Mooooooooooo!",
#    ]
#
#    connection {
#      type        = "ssh"
#      user        = "ubuntu"
#      private_key = tls_private_key.hashicat.private_key_pem
#      host        = aws_eip.hashicat.public_ip
#    }
#  }
#}
#
#resource "tls_private_key" "hashicat" {
#  algorithm = "ED25519"
#}
#
#locals {
#  private_key_filename = "${var.prefix}-ssh-key.pem"
#}
#
#resource "aws_key_pair" "hashicat" {
#  key_name   = local.private_key_filename
#  public_key = tls_private_key.hashicat.public_key_openssh
#}

resource "aws_route_table" "public_subnet_rt" {
  count = 2
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "${var.prefix}-${var.env}-public-subnet-rt-${count.index}"
  }
}

resource "aws_route" "public_subnet_rt_association" {
  count = 2
  route_table_id         = aws_route_table.public_subnet_rt[count.index].id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.igw.id
}

resource "aws_route_table_association" "public_subnet_assoc" {
  count       = 2
  subnet_id   = aws_subnet.public_subnet[count.index].id
  route_table_id = aws_route_table.public_subnet_rt[count.index].id
}

resource "aws_route_table" "private_subnet_rt_2a" {
  count = 3
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "${var.prefix}-${var.env}-private-subnet-rt-2a-${count.index}"
  }
}

resource "aws_route" "private_subnet_rt_2a_association" {
  count = 3
  route_table_id         = aws_route_table.private_subnet_rt_2a[count.index].id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_instance.nat_instance_2a[count.index].id
}

resource "aws_route_table_association" "private_subnet_2a_assoc" {
  count       = 3
  subnet_id   = aws_subnet.private_subnet-2a[count.index].id
  route_table_id = aws_route_table.private_subnet_rt_2a[count.index].id
}

resource "aws_route_table" "private_subnet_rt_2c" {
  count = 3
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "${var.prefix}-${var.env}-private-subnet-rt-2c-${count.index}"
  }
}

resource "aws_route" "private_subnet_rt_2c_association" {
  count = 3
  route_table_id         = aws_route_table.private_subnet_rt_2c[count.index].id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_instance.nat_instance_2c[count.index].id
}

resource "aws_route_table_association" "private_subnet_2c_assoc" {
  count       = 3
  subnet_id   = aws_subnet.private_subnet-2c[count.index].id
  route_table_id = aws_route_table.private_subnet_rt_2c[count.index].id
}


############### NAT Instance & Keypair ###############

resource "aws_instance" "nat_instance_2a" {
  count      = 1
  ami        = "ami-ami-01056eaaa603955a4"  # 이 부분은 실제 AMI ID로 변경해야 합니다.
  instance_type = "t3.medium"
  subnet_id  = aws_subnet.public_subnet[0].id
  key_name   = var.key  # 필요한 경우 키 이름으로 변경
  tags = {
    Name = "${var.prefix}-${var.env}-nat-instance-2a"
  }
}

resource "aws_instance" "nat_instance_2c" {
  count      = 1
  ami        = "ami-ami-01056eaaa603955a4"  # 이 부분은 실제 AMI ID로 변경해야 합니다.
  instance_type = "t3.medium"
  subnet_id  = aws_subnet.public_subnet[1].id
  key_name   = var.key  # 필요한 경우 키 이름으로 변경
  tags = {
    Name = "${var.prefix}-${var.env}-nat-instance-2c"
  }
}

resource "aws_key_pair" "key" {
  key_name   = var.key  # 원하는 키 페어의 이름으로 변경
  public_key = file("~/.ssh/id_rsa.pub")  # 퍼블릭 키의 경로를 지정
}
