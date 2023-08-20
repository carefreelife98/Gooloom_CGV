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

#resource "aws_subnet" "hashicat" {
#  vpc_id     = aws_vpc.vpc.id
#  cidr_block = var.subnet_prefix
#
#  tags = {
#    name = "${var.prefix}-subnet"
#  }
#}

#resource "aws_subnet" "public_subnet" {
#  count             = var.num_azs * 2
##  name = "${var.prefix}-${var.env}-CGV-sub-pub${count.index / 2}"
#  vpc_id            = aws_vpc.vpc.id
#  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index)
#  availability_zone = element(var.azs, count.index / 2)
#  map_public_ip_on_launch = true
#
#  tags = {
#    Name = "${var.prefix}-${var.env}-sub-pub"
#  }
#}
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
    Name = "${var.prefix}-${var.env}-sub-${element(var.svc, count.index)}-pri"
  }
}

resource "aws_subnet" "private_subnet-2c" {
  count             = 3
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, (count.index * 1) + 12)
  availability_zone = var.azs[1]

  tags = {
    Name = "${var.prefix}-${var.env}-sub-${element(var.svc, count.index)}-pri"
  }
}
#resource "aws_subnet" "private_subnet" {
#  count             = length(local.subnet_names)
#  vpc_id            = aws_vpc.vpc.id
#  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index + 16)
#  availability_zone = element(var.azs, count.index / length(var.svc))
#  tags = {
#    Name = local.subnet_names[count.index]
#  }
#}

#resource "aws_security_group" "sg" {
#  name = "${var.prefix}-${var.env}-CGV-sg-web"
#
#  vpc_id = aws_vpc.vpc.id
#
#  ingress {
#    from_port   = 22
#    to_port     = 22
#    protocol    = "tcp"
#    cidr_blocks = ["0.0.0.0/0"]
#  }
#
#  ingress {
#    from_port   = 80
#    to_port     = 80
#    protocol    = "tcp"
#    cidr_blocks = ["0.0.0.0/0"]
#  }
#
#  ingress {
#    from_port   = 443
#    to_port     = 443
#    protocol    = "tcp"
#    cidr_blocks = ["0.0.0.0/0"]
#  }
#
#  egress {
#    from_port       = 0
#    to_port         = 0
#    protocol        = "-1"
#    cidr_blocks     = ["0.0.0.0/0"]
#    prefix_list_ids = []
#  }
#
#  tags = {
#    Name = "${var.prefix}-security-group"
#  }
#}

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

#resource "aws_route_table_association" "rt_association" {
#  subnet_id      = aws_subnet.public_subnet.id
#  route_table_id = aws_route_table.rt.id
#}

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
