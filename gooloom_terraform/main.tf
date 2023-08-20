terraform {
  cloud {
    organization = "Gooloom"
    workspaces {
      name = "Gooloom_CGV"
    }
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.10.0"
    }
  }
}

provider "aws" {
  region = var.region
}

resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr
}

resource "aws_subnet" "public_subnet" {
  count             = var.num_azs * 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index)
  availability_zone = element(var.azs, count.index / 2)
  map_public_ip_on_launch = true

  tags = {
    Name = "public-subnet-${element(var.azs, count.index / 2)}-${count.index % 2 + 1}"
  }
}

resource "aws_subnet" "private_subnet" {
  count             = var.num_azs * 2 * var.num_private_subnets
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index + 8)
  availability_zone = element(var.azs, count.index / (2 * var.num_private_subnets))

  tags = {
    Name = "private-subnet-${element(var.azs, count.index / (2 * var.num_private_subnets))}-${count.index % var.num_private_subnets + 1}"
  }
}

resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id
}

resource "aws_lb" "alb" {
  name               = "my-alb"
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

