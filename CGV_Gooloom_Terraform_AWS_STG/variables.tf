##############################################################################
# Variables File
#
# Here is where we store the default values for all the variables used in our
# Terraform code. If you create a variable with no default, the user will be
# prompted to enter it (or define it via config file or command line flags.)

variable "prefix" {
  description = "This prefix will be included in the name of most resources."
  default     = "GooLoom"
}

variable "region" {
  description = "The region where the resources are created."
  default     = "ap-northeast-2"
}

variable "address_space" {
  description = "The address space that is used by the virtual network. You can supply more than one address space. Changing this forces a new resource to be created."
  default     = "10.0.0.0/16"
}

variable "instance_type" {
  description = "Specifies the AWS instance type."
  default     = "t3.medium"
}

variable "admin_username" {
  description = "Administrator user name for mysql"
  default     = "carefreelife"
}

variable "AWS_ACCESS_KEY_ID" {
  type = string
  default = "it will be overwrite"
}

variable "AWS_SECRET_ACCESS_KEY" {
  type = string
  default = "it will be overwrite"
}




# ChatGPT
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

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
}



# variable "key" {
#  description = "SSH-keypair-NAT"
#  type        = string
#  default     = "PRD"
#}

# Custom
variable "env" {
  description = "DEV / STG / PRD"
  type        = string
  default     = "STG"
}

variable "svc" {
  description = "web, was / db"
  type        = list(string)
  default     = ["default"]
}