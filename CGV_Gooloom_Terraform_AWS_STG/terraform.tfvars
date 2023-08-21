region         = "ap-northeast-2"
vpc_cidr       = "20.0.0.0/16"
azs            = ["ap-northeast-2a", "ap-northeast-2c"]
num_azs        = 2
num_private_subnets = 2

env            = "STG"
svc            = ["web-was", "db"]