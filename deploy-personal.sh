#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CORE_ENV_DIR="$ROOT_DIR/infra/envs/personal/core"
APP_ENV_DIR="$ROOT_DIR/infra/envs/personal/app"

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

echo "==> Step 4: Export Jib env vars and build/push images"

cd "$ROOT_DIR"

export SALESANALYZERJOB_ECR_REPO="$SALES_ANALYZER_JOB_ECR_URL"
export SALESCLEANUP_ECR_REPO="$SALES_CLEANUP_ECR_URL"
export SALESDASHBOARD_ECR_REPO="$SALES_DASHBOARD_ECR_URL"
export SALESFXSERVICE_ECR_REPO="$SALES_FX_SERVICE_ECR_URL"
export SALESINGESTER_ECR_REPO="$SALES_INGESTER_ECR_URL"

./gradlew \
  :SalesIngester:jib \
  :SalesAnalyzerJob:jib \
  :SalesCleanup:jib \
  :SalesFxService:jib \
  :SalesDashboard:jib

echo "==> Step 5: Apply app infra (personal)"

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