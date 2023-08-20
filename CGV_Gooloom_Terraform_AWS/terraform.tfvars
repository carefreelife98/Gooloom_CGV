region         = "ap-northeast-2"
vpc_cidr       = "10.0.0.0/16"
azs            = ["ap-northeast-2a", "ap-northeast-2c"]
num_azs        = 2
num_private_subnets = 3

env            = "PRD"
svc            = ["web", "was", "db"]
