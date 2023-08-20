variable "region" {
  description = "AWS region"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
}

variable "azs" {
  description = "Availability Zones"
  type        = list(string)
}

variable "num_azs" {
  description = "Number of Availability Zones"
  type        = number
  default     = 2
}

variable "num_private_subnets" {
  description = "Number of private subnets per Availability Zone"
  type        = number
  default     = 3
}

