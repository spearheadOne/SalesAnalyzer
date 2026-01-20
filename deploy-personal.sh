#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CORE_ENV_DIR="$ROOT_DIR/infra/envs/personal/core"
APP_ENV_DIR="$ROOT_DIR/infra/envs/personal/app"
PLATFORM="${PLATFORM:-linux/arm64}"
TAG="$(./gradlew -q printVersion)"
BUILDX_OUT="type=registry,oci-mediatypes=false"
ATTEST_FLAGS=(--provenance=false --sbom=false)

echo "==> Step 1: Apply core infra (personal)"

cd "$CORE_ENV_DIR"

if [ ! -d ".terraform" ]; then
  echo "    - terraform init (core)"
  terraform init
fi

echo "    - terraform apply (core)"
terraform apply -auto-approve

echo "==> Step 2: Read core outputs (ECR + region)"

ECR_REGISTRY=$(terraform output -raw ecr_registry)
SALES_ANALYZER_JOB_ECR_URL=$(terraform output -raw sales_analyzer_job_ecr_url)
SALES_CLEANUP_ECR_URL=$(terraform output -raw sales_cleanup_ecr_url)
SALES_DASHBOARD_ECR_URL=$(terraform output -raw sales_dashboard_ecr_url)
SALES_FX_SERVICE_ECR_URL=$(terraform output -raw sales_fx_service_ecr_url)
SALES_INGESTER_ECR_URL=$(terraform output -raw sales_ingester_ecr_url)
API_GATEWAY_INVOKE_URL=$(terraform output -raw upload_invoke_url)

echo "==> Step 3: Login to ECR"

aws ecr get-login-password --region eu-west-1 \
  | docker login --username AWS --password-stdin "$ECR_REGISTRY"

echo "    - ECR login OK"

echo "==> Step 4: Build artifacts for non-lambda apps and generate dockerfiles"
cd "$ROOT_DIR"

./gradlew clean

./gradlew \
  :SalesDashboard:assemble \
  :SalesDashboard:dockerfile \
  :SalesFxService:assemble \
  :SalesFxService:dockerfile \
  :SalesAnalyzerJob:assemble \
  :SalesAnalyzerJob:dockerfile \
  :SalesCleanup:buildNativeLayersTask \
  :SalesCleanup:dockerPrepareContext \
  :SalesCleanup:dockerfileNative \
  :SalesIngester:buildNativeLayersTask \
  :SalesIngester:shadowJar

DOCKERFILE_SALES_DASHBOARD="$ROOT_DIR/SalesDashboard/build/docker/main/Dockerfile"
DOCKERFILE_SALES_FX_SERVICE="$ROOT_DIR/SalesFxService/build/docker/main/Dockerfile"
DOCKERFILE_SALES_ANALYZER_JOB="$ROOT_DIR/SalesAnalyzerJob/build/docker/main/Dockerfile"
DOCKERFILE_SALES_CLEANUP="$ROOT_DIR/SalesCleanup/build/docker/native-main/DockerfileNative"
DOCKERFILE_SALES_INGESTER="$ROOT_DIR/SalesIngester/Dockerfile"

CTX_SALES_DASHBOARD="$ROOT_DIR/SalesDashboard/build/docker/main"
CTX_SALES_FX_SERVICE="$ROOT_DIR/SalesFxService/build/docker/main"
CTX_SALES_ANALYZER_JOB="$ROOT_DIR/SalesAnalyzerJob/build/docker/main"
CTX_SALES_CLEANUP="$ROOT_DIR/SalesCleanup/build/docker/native-main"
CTX_SALES_INGESTER="$ROOT_DIR/SalesIngester"

echo "==> Step 5: Build & push images with Docker buildx"

echo "    - SalesCleanup (native)"
docker buildx build --progress=plain --platform "$PLATFORM" \
  "${ATTEST_FLAGS[@]}" \
  --output="$BUILDX_OUT" \
  -f "$DOCKERFILE_SALES_CLEANUP" \
  -t "${SALES_CLEANUP_ECR_URL}:${TAG}" \
  "$CTX_SALES_CLEANUP"

echo "    - SalesIngester (JVM Lambda)"
docker buildx build --progress=plain --platform "$PLATFORM" \
  "${ATTEST_FLAGS[@]}" \
  --output="$BUILDX_OUT" \
  -f "$DOCKERFILE_SALES_INGESTER" \
  -t "${SALES_INGESTER_ECR_URL}:${TAG}" \
  "$CTX_SALES_INGESTER"

echo "    - SalesDashboard (JVM Fargate)"
docker buildx build --progress=plain --platform "$PLATFORM" \
  "${ATTEST_FLAGS[@]}" \
  --output="$BUILDX_OUT" \
  -f "$DOCKERFILE_SALES_DASHBOARD" \
  -t "${SALES_DASHBOARD_ECR_URL}:${TAG}" \
  "$CTX_SALES_DASHBOARD"

echo "    - SalesFxService (JVM Fargate)"
docker buildx build --progress=plain --platform "$PLATFORM" \
  "${ATTEST_FLAGS[@]}" \
  --output="$BUILDX_OUT" \
  -f "$DOCKERFILE_SALES_FX_SERVICE" \
  -t "${SALES_FX_SERVICE_ECR_URL}:${TAG}" \
  "$CTX_SALES_FX_SERVICE"

echo "    - SalesAnalyzerJob (JVM Fargate)"
docker buildx build --progress=plain --platform "$PLATFORM" \
  "${ATTEST_FLAGS[@]}" \
  --output="$BUILDX_OUT" \
  -f "$DOCKERFILE_SALES_ANALYZER_JOB" \
  -t "${SALES_ANALYZER_JOB_ECR_URL}:${TAG}" \
  "$CTX_SALES_ANALYZER_JOB"

echo "==> Step 6: Apply app infra (personal)"

cd "$APP_ENV_DIR"

if [ ! -d ".terraform" ]; then
  echo "    - terraform init (app)"
  terraform init
fi

echo "    - terraform apply (app)"
terraform apply -auto-approve

SALES_DASHBOARD_URL=$(terraform output -raw sales_dashboard_url)

echo "API GATEWAY INVOKE URL: $API_GATEWAY_INVOKE_URL"
echo "SALES DASHBOARD URL: $SALES_DASHBOARD_URL"

echo "==> All done."