region         = "ap-northeast-3"
vpc_cidr       = "20.0.0.0/16"
azs            = ["ap-northeast-3a", "ap-northeast-3c"]
num_azs        = 2
num_private_subnets = 2

env            = "STG"
svc            = ["web", "was", "db"]