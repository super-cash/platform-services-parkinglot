echo ">>> Building project .jar..."
gradle build -x test

echo ">>> Login into AWS ECR..."
$(aws ecr get-login --no-include-email --region sa-east-1 --profile=supercash-docker)

echo ">>> Building project container..."
docker build -t supercash/distance-matrix-service .

echo ">>> Tagging repository..."
docker tag supercash/distance-matrix-service:latest 457706112565.dkr.ecr.sa-east-1.amazonaws.com/supercash/distance-matrix-service:latest

echo ">>> Pushing to ECR..."
docker push 457706112565.dkr.ecr.sa-east-1.amazonaws.com/supercash/distance-matrix-service:latest
